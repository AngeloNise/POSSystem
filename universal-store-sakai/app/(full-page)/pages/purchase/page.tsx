'use client';
import React, { useEffect, useState, useRef } from "react";
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Card } from 'primereact/card';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { Dialog } from 'primereact/dialog';
import { MultiSelect } from 'primereact/multiselect';
import { Paginator } from 'primereact/paginator';
import { Ripple } from 'primereact/ripple';
import { Dropdown } from 'primereact/dropdown';
import { classNames } from 'primereact/utils';
import { useRouter } from 'next/navigation';
import { ConfirmDialog } from 'primereact/confirmdialog';
import {ProgressSpinner} from "primereact/progressspinner";

import useAxiosInstance from '@/util/CustomAxios'

import useAuthStore from '@/store/AuthStore';
import PurchaseService from "@/service/access_management/PurchaseService";

const POSPurchase = () => {
    const axiosInstance = useAxiosInstance();
    const paginator = useRef(null);
    const purchaseService = new PurchaseService();
    const userInfo = useAuthStore((state) => state.userInfo);
    const toast = useRef(null);
    const [products, setProducts] = useState<any[]>([]);
    const [search, setSearch] = useState("");
    const [receipt, setReceipt] = useState([]);
    const [currentTime, setCurrentTime] = useState("");
    const [amountPaid, setAmountPaid] = useState('');
    const [receiptNumber, setReceiptNumber] = useState("0000");
    const [displaySerialModal, setDisplaySerialModal] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [serialNumbers, setSerialNumbers] = useState([]);
    const [selectedSerials, setSelectedSerials] = useState([]);
    const calculateTotal = () => receipt.reduce((total, item) => total + (item.qty * item.price), 0);
    const calculateChange = () => Number(amountPaid) - calculateTotal();
    const [tableStart, setTableStart] = useState(0);
    const [tableLimit, setTableLimit] = useState(10);
    const [tableTotalCount, setTableTotalCount] = useState(0);
    const [tableLoading, setTableLoading] = useState(false);
    const [serialSearchTerm, setSerialSearchTerm] = useState('');
    const [loading, setLoading] = useState(false);
    const [visible, setVisible] = useState(false);
    const [selectedImage, setSelectedImage] = useState(null);
    const [serialSearch, setSerialSearch] = useState('');
    const [visibleConfirmFinalize, setVisibleConfirmFinalize] = useState(false);
    const debounceTimeout = useRef(null);
    const router = useRouter();
    const searchTimeout = useRef<NodeJS.Timeout | null>(null);
    const createdDateFormatted = new Date().toISOString().replace('T', ' ').substring(0, 19);

    const [queryParams, setQueryParams] = useState({
        "name": "",
        "serialNumber": ""
    });

    useEffect(() => {
        queryProductList(0, 10, false, false);
        setCurrentTime(new Date().toLocaleTimeString());
        const timer = setInterval(() => setCurrentTime(new Date().toLocaleTimeString()), 1000);
        return () => clearInterval(timer);
    }, []);

    useEffect(() => {
        const fetchLatestReceiptNumber = async () => {
            try {
                const response = await purchaseService.getLatestReceiptNumber();
                setReceiptNumber(response.data);
            } catch (error) {
                console.error("Error fetching receipt number:", error);
            }
        };
        fetchLatestReceiptNumber();
    }, []);

    const queryProductList = (start, limit, refresh, search) => {
        setTableLoading(true);

        // Use passed search string if it's a string, else fallback to queryParams.name
        const keyword = typeof search === 'string'
            ? search.trim()
            : queryParams.name?.trim() || '';

        if (!keyword) {
            setProducts([]);
            setTableTotalCount(0);
            setTableLoading(false);
            return;
        }

        const params = {
            name: keyword,
            serialNumber: keyword,
            start,
            limit
        };

        purchaseService.list(params)
            .then((res) => {
                const allProducts = res.data.data;

                const filteredProducts = allProducts.filter(product => {
                    if (!product.serialize) {
                        return product.stocks > 0;
                    } else {
                        // For serialized products, check available serials not sold
                        const availableSerials = allProducts.filter(p => p.id === product.id && p.status !== 'sold');
                        return availableSerials.length > 0;
                    }
                });

                const uniqueProductIds = [...new Set(filteredProducts.map(product => product.id))];

                setProducts(filteredProducts);
                setTableTotalCount(uniqueProductIds.length);
                setTableStart(start);
                setTableLoading(false);
            })
            .catch((error) => {
                if (error.response?.status === 401) {
                    router.push("/auth/login");
                } else {
                    toast.current.show({ severity: 'error', detail: "Error fetching product list", life: 3000 });
                    setTableLoading(false);
                }
            });
    };

    const updateReceiptQuantity = (rowData, type, serialNumbers) => {
        if (Array.isArray(serialNumbers)) {
            let updatedReceipt = [...receipt];
            serialNumbers.forEach((serialNumber) => {
                const productIndex = updatedReceipt.findIndex(item => item.id === rowData.id && item.serialNumber === serialNumber);

                if (productIndex !== -1) {
                    if (type === 'increase' && updatedReceipt[productIndex].qty < rowData.stocks) {
                        updatedReceipt[productIndex].qty += 1;
                    } else if (type === 'decrease') {
                        if (updatedReceipt[productIndex].qty > 0) {
                            updatedReceipt[productIndex].qty -= 1;
                        }
                        if (updatedReceipt[productIndex].qty === 0) {
                            updatedReceipt.splice(productIndex, 1);
                        }
                    }
                } else if (type === 'increase' && rowData.stocks > 0) {
                    updatedReceipt.push({ ...rowData, qty: 1, serialNumber });
                }
            });

            setReceipt(updatedReceipt);
        } else {
            console.error('serialNumbers is not an array:', serialNumbers);
        }
    };

    const finalizeTransaction = async () => {
        if (receipt.length === 0) {
            toast.current.show({
                severity: 'warn',
                summary: 'Empty Receipt',
                detail: 'No items in the receipt.',
                life: 3000
            });
            return;
        }

        const receiptData = {
            receiptNumber,
            totalPrice: calculateTotal(),
            createdDate: createdDateFormatted,
            createdBy: userInfo.ACCOUNT_NAME,
            counter: userInfo.COUNTER,
            receiptItems: receipt.map(item => ({
                productId: item.id,
                productName: item.name,
                serialNumber: item.serialize ? item.serialNumber : null,
                price: item.price,
                quantity: item.qty,
                createdDate: createdDateFormatted
            }))
        };

        try {
            const response = await purchaseService.finalizeTransaction(receiptData);

            if (response.status === 200) {
                toast.current.show({
                    severity: 'success',
                    summary: 'Success',
                    detail: 'Transaction completed successfully!',
                    life: 3000
                });

                setReceipt([]);
                setAmountPaid("0");
                setReceiptNumber((prev) => String(parseInt(prev) + 1).padStart(4, '0'));

                setSearch("");
                setQueryParams({ name: "", serialNumber: "" });
                setProducts([]);
                setTableTotalCount(0);
                setTableStart(0);
            } else {
                toast.current.show({
                    severity: 'error',
                    summary: 'Transaction Failed',
                    detail: response.data,
                    life: 3000
                });
            }
        } catch (error) {
            console.error("Error finalizing transaction:", error);
            toast.current.show({
                severity: 'error',
                summary: 'Error',
                detail: 'An error occurred while processing the transaction.',
                life: 3000
            });
        }
    };

    const handleProductNameClick = (product) => {
        if (product.serialize) {
            setSelectedProduct(product);

            // Get serial numbers that are available (not sold)
            const availableSerialNumbers = (Array.isArray(products) ? products : [])
                .filter(p => p.name === product.name && p.id === product.id && p.status !== 'sold')
                .map(p => p.serialNumber);

            setSerialNumbers(availableSerialNumbers);

            const existingSerials = receipt
                .filter(item => item.id === product.id)
                .map(item => item.serialNumber);

            setSelectedSerials(existingSerials);
            setDisplaySerialModal(true);
        }
    };

    const onPageChange = (e) => {
        setTableStart(e.first);
        setTableLimit(e.rows);
        queryProductList(e.first, e.rows, false, false);
    }

    const paginatorTemplate = {
        layout: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown CurrentPageReport',
        'PrevPageLink': (options) => {
            return (
                <button type="button" className={options.className} onClick={options.onClick} disabled={options.disabled}>
                    <span className="p-3">Previous</span>
                    <Ripple />
                </button>
            )
        },
        'NextPageLink': (options) => {
            return (
                <button type="button" className={options.className} onClick={options.onClick} disabled={options.disabled}>
                    <span className="p-3">Next</span>
                    <Ripple />
                </button>
            )
        },
        'PageLinks': (options) => {
            if ((options.view.startPage === options.page && options.view.startPage !== 0) || (options.view.endPage === options.page && options.page + 1 !== options.totalPages)) {
                const className = classNames(options.className, { 'p-disabled': true });

                return <span className={className} style={{ userSelect: 'none' }}>...</span>;
            }

            return (
                <button type="button" className={options.className} onClick={options.onClick}>
                    {options.page + 1}
                    <Ripple />
                </button>
            )
        },
        'RowsPerPageDropdown': (options) => {
            const dropdownOptions = [
                { label: 10, value: 10 },
                { label: 25, value: 25 },
                { label: 50, value: 50 },
                { label: 100, value: 100 }
            ];

            return <Dropdown value={options.value} options={dropdownOptions} onChange={options.onChange} />;
        },
        'CurrentPageReport': (options) => {
            return (
                <span style={{ color: 'var(--text-color)', userSelect: 'none', width: '120px', textAlign: 'center' }}>
                    {options.first} - {options.last} of {options.totalRecords}
                </span>
            )
        }
    };

    const footerDialog = (
        <div className="flex justify-content-end">
            <Button label="Cancel" icon="pi pi-times" onClick={() => setDisplaySerialModal(false)} className="p-button-text" />
            <Button label="Save" icon="pi pi-check" onClick={() => {
                setReceipt(prevReceipt => {
                    let updatedReceipt = prevReceipt.filter(
                        item => !(item.id === selectedProduct.id && !selectedSerials.includes(item.serialNumber))
                    );
                    selectedSerials.forEach(serialNumber => {
                        if (!updatedReceipt.some(item => item.id === selectedProduct.id && item.serialNumber === serialNumber)) {
                            updatedReceipt.push({ ...selectedProduct, qty: 1, serialNumber });
                        }
                    });
                    return updatedReceipt;
                });
                setDisplaySerialModal(false);
            }} />
        </div>
    );

    useEffect(() => {
        if (search.trim() !== "") {
            queryProductList(0, tableLimit, false, true);
        } else {
            setProducts([]);
            setTableTotalCount(0);
        }
    }, [tableLimit])

    const handleSearchChange = (e) => {
        const value = e.target.value;
        setSearch(value);
        setQueryParams((prev) => ({ ...prev, name: value }));

        if (searchTimeout.current) {
            clearTimeout(searchTimeout.current);
        }

        searchTimeout.current = setTimeout(() => {
            if (value.trim() !== "") {
                // Pass 'value' directly instead of 'true' to queryProductList
                queryProductList(0, tableLimit, false, value);
            } else {
                setProducts([]);
                setTableTotalCount(0);
            }
        }, 800);
    };

    const openDialog = (image) => {
        setSelectedImage(`/product-image/${image?.image || 'Placeholder.jpg'}`);
        setVisible(true);
    };

    const stockImageTemplate = (image) => {
        return (
            <div
                style={{
                    borderRadius: '35px',
                    display: 'flex',
                    justifyContent: 'flex-start', // <- aligns image to the left
                    alignItems: 'center',
                    height: '100%',
                    cursor: 'pointer',
                    padding: '0.5rem' // optional for spacing
                }}
                onClick={() => openDialog(image)}
            >
                <img
                    src={`/product-image/${image?.image || 'Placeholder.jpg'}`}
                    alt="Product"
                    width="90"
                    height="60"
                    style={{ borderRadius: '35px', objectFit: 'cover' }}
                />
            </div>
        );
    };

    const onConfirmFinalize = () => {
        finalizeTransaction();
        setVisibleConfirmFinalize(false); // Close the dialog after finalization
    };

    const handleSerialSearch = (e) => {
        const keyword = e.target.value;
        setSerialSearchTerm(keyword);

        if (debounceTimeout.current) {
            clearTimeout(debounceTimeout.current);
        }

        debounceTimeout.current = setTimeout(async () => {
            setLoading(true);
            try {
                const response = await purchaseService.searchSerialNumbers(selectedProduct.id, keyword);
                setSerialNumbers(response.data);
            } catch (error) {
                console.error('Error searching serials:', error);
            } finally {
                setLoading(false);
            }
        }, 300);
    };


    return (
        <div className="grid p-3">
            <Toast ref={toast} />

            <ConfirmDialog
                visible={visibleConfirmFinalize}
                onHide={() => setVisibleConfirmFinalize(false)}
                message="Finalize this Purchase"
                icon="pi pi-check-circle"
                accept={onConfirmFinalize}
                reject={() => setVisibleConfirmFinalize(false)}
            />

            <div className="field col-12 md:col-8">
                <div className="sticky w-full bg-white p-3 shadow-md z-1">
                    <InputText
                        placeholder="Search by Product Name/Serial Number"
                        value={search}
                        onChange={handleSearchChange}
                        className="w-full px-4 border border-gray-300 rounded-lg"
                    />

                </div>
                <Card className="p-2" style={{ maxHeight: '78vh', overflowY: 'auto' }}>
                    <DataTable
                        value={
                            search.trim()
                                ? products
                                    .filter(p =>
                                        p.name.toLowerCase().includes(search.toLowerCase()) ||
                                        (p.serialNumber && p.serialNumber.toLowerCase().includes(search.toLowerCase()))
                                    )
                                    .reduce((uniqueProducts, currentProduct) => {
                                        if (!uniqueProducts.some(p => p.id === currentProduct.id)) {
                                            uniqueProducts.push(currentProduct);
                                        }
                                        return uniqueProducts;
                                    }, [])
                                : products.reduce((uniqueProducts, currentProduct) => {
                                    if (!uniqueProducts.some(p => p.id === currentProduct.id)) {
                                        uniqueProducts.push(currentProduct);
                                    }
                                    return uniqueProducts;
                                }, [])
                        }
                        emptyMessage={search.trim() ? "No results found" : "Use Search Bar"}
                    >

                        <Column field="image" header="Image" style={{ minWidth: '10rem' }} body={stockImageTemplate} bodyStyle={{ padding: '1rem', textAlign: 'left' }}/>
                        <Column field="name" header="Product Name" body={(rowData) => (
                            <span
                                onClick={() => rowData.serialNumber && handleProductNameClick(rowData)}
                                style={{
                                    cursor: rowData.serialNumber ? 'pointer' : 'default',
                                    color: rowData.serialNumber ? 'blue' : 'inherit',
                                    textDecoration: rowData.serialNumber ? 'underline' : 'none'
                                }}
                            >
                                {rowData.serialNumber ? '' : ''}{rowData.name}
                            </span>
                        )} />

                        <Column field="serialNumber" header="Serial Number"   body={(rowData) => rowData.serialNumber ? "SN•••••" : ""}/>
                        <Column field="stocks" header="Stocks" body={(rowData) =>
                            rowData.serialize ? rowData.stocks : rowData.stocks
                        } />

                        <Column
                            header="Quantity"
                            body={(rowData) => {
                                const productInReceipt = receipt.find(item => item.id === rowData.id) || null;
                                const buttonLabel = productInReceipt ? "Edit" : "Add";

                                const handleAddEditClick = () => {
                                    setSelectedProduct(rowData);

                                    // Get all serialized products with the same name *and* id
                                    const serialNumbersForProduct = products
                                        .filter(p => p.serialize && p.id === rowData.id && p.name === rowData.name)
                                        .map(p => p.serialNumber);

                                    setSerialNumbers(serialNumbersForProduct);

                                    // Get all serial numbers already added to receipt for this product
                                    const existingSerials = receipt
                                        .filter(item => item.id === rowData.id && item.name === rowData.name)
                                        .map(item => item.serialNumber);

                                    setSelectedSerials(existingSerials);
                                    setDisplaySerialModal(true);
                                };



                                return (
                                    <div className="flex items-center justify-center h-full">
                                        {rowData.serialize ? (
                                            <Button
                                                label={buttonLabel}
                                                onClick={handleAddEditClick}
                                                className={productInReceipt ? "p-button-outlined p-button-warning" : "p-button-outlined p-button-primary"}
                                                style={{
                                                    fontSize: '12px',
                                                    padding: '6px 10px',
                                                    borderRadius: '4px',
                                                    marginLeft: '15px'
                                                }}
                                            />
                                        ) : (
                                            <div className="flex items-center space-x-2">
                                                <Button
                                                    icon="pi pi-minus"
                                                    onClick={() => updateReceiptQuantity(rowData, 'decrease', [rowData.serialNumber])}
                                                    disabled={!productInReceipt}
                                                    className="p-button-outlined p-button-danger p-button-rounded"
                                                    style={{ width: '24px', height: '24px', fontSize: '12px', padding: '2px' }}
                                                />
                                                <span className="px-2 flex items-end">{productInReceipt ? productInReceipt.qty : 0}</span>
                                                <Button
                                                    icon="pi pi-plus"
                                                    onClick={() => updateReceiptQuantity(rowData, 'increase', [rowData.serialNumber])}
                                                    disabled={rowData.stocks <= 0 || (productInReceipt ? productInReceipt.qty >= rowData.stocks : false)}
                                                    className="p-button-outlined p-button-success p-button-rounded"
                                                    style={{ width: '24px', height: '24px', fontSize: '12px', padding: '2px' }}
                                                />

                                            </div>
                                        )}
                                    </div>
                                );
                            }}
                        />
                    </DataTable>
                    <Paginator ref={paginator} template={paginatorTemplate} first={tableStart} rows={tableLimit} totalRecords={tableTotalCount}
                               onPageChange={(e) => onPageChange(e)} />
                </Card>
            </div>


            <div className="field col-8 md:col-4">
                <Card className="p-2" style={{ maxHeight: '85.7vh', overflowY: 'auto' }}>
                    <h2 className="text-xl font-bold mb-4">Receipt</h2>
                    <p className="mb-2"><strong>DATE:</strong> {new Date().toLocaleDateString('en-CA')}</p>
                    <p className="mb-2"><strong>TIME:</strong> {currentTime}</p>
                    <p className="mb-2"><strong>Receipt #:</strong> {receiptNumber}</p>

                    <DataTable value={receipt}>
                        <Column
                            header=""
                            body={(rowData) => (
                                <Button
                                    icon="pi pi-minus"
                                    onClick={() => updateReceiptQuantity(rowData, 'decrease', [rowData.serialNumber])}
                                    disabled={rowData.qty <= 0}
                                    className="p-button-outlined p-button-danger p-button-rounded"
                                    style={{ width: '24px', height: '24px', fontSize: '12px', padding: '2px' }}
                                />
                            )}
                        />
                        <Column field="name" header="ITEM" />
                        <Column field="serialNumber" header="Serial #" />
                        <Column field="qty" header="QTY" />
                        <Column field="price" header="UNIT PRICE" body={(rowData) => rowData.price.toFixed(2)} />
                        <Column header="AMOUNT" body={(rowData) => (rowData.qty * rowData.price).toFixed(2)} />
                    </DataTable>

                    <div className="flex justify-content-between mt-4 mb-2">
                        <strong>Total: </strong><span>{calculateTotal().toFixed(2)}</span>
                    </div>
                    <div className="flex justify-content-between mb-4">
                        <strong>Amount Paid: </strong>
                        <InputText
                            value={amountPaid}
                            onChange={(e) => setAmountPaid(e.target.value)}
                            className="w-32"
                            type="number"
                            min={0}
                            placeholder="0"
                        />
                    </div>
                    <div className="flex justify-content-between mb-4">
                        <strong>Change: </strong><span>{calculateChange().toFixed(2)}</span>
                    </div>
                    <div className="flex justify-content-between">
                        <Button label="Finalize Transaction" className="p-button-success" onClick={() => setVisibleConfirmFinalize(true)}/>
                    </div>
                </Card>
            </div>
            <Dialog
                visible={displaySerialModal}
                header={`Select Serial Number for ${selectedProduct ? selectedProduct.name : ''}`}
                modal
                footer={footerDialog}
                onHide={() => { setDisplaySerialModal(false); setSerialSearch(''); }}
                style={{ width: '35vw' }}
            >
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    {/* Selected serial numbers */}
                    <div
                        style={{
                            maxHeight: '35%',
                            overflowY: 'auto',
                            border: '1px solid #ccc',
                            padding: '8px',
                            borderRadius: '5px',
                            display: 'flex',
                            flexWrap: 'wrap',
                            gap: '5px',
                        }}
                    >
                        {selectedSerials.map((sn) => (
                            <div
                                key={sn}
                                style={{
                                    background: '#e3e3e3',
                                    padding: '5px 10' +
                                        'px',
                                    borderRadius: '12px',
                                    display: 'inline-flex',
                                    alignItems: 'center',
                                    cursor: 'pointer',
                                }}
                                onClick={() => setSelectedSerials(selectedSerials.filter((s) => s !== sn))}
                            >
                                {sn} &times;
                            </div>
                        ))}
                    </div>

                    {/* MultiSelect with latest serial numbers */}
                    <MultiSelect
                        value={selectedSerials}
                        options={serialNumbers
                            .filter(sn => sn != null)
                            .map(sn => ({ label: String(sn), value: sn }))}
                        onChange={(e) => setSelectedSerials(e.value)}
                        placeholder="Select Serial Numbers"
                        display="chip"
                        filter={false} // disable default filter
                        maxSelectedLabels={3}
                        className="w-full md:w-17rem"
                        style={{ width: '100%' }}
                        panelHeaderTemplate={
                            <div style={{ padding: '0.5rem' }}>
                                <InputText
                                    placeholder="Search Serial..."
                                    className="w-full"
                                    type="text"
                                    id="inputProductName"
                                    value={serialSearchTerm}
                                    style={{ width: '100%' }}
                                    onChange={handleSerialSearch}
                                />
                            </div>
                        }
                        panelFooterTemplate={
                            loading ? (
                                <ProgressSpinner style={{ width: '20px', height: '20px' }} />
                            ) : null
                        }
                    />
                </div>
            </Dialog>

            <Dialog
                header="Product Image"
                visible={visible}
                style={{ width: 'auto' }}
                modal
                onHide={() => setVisible(false)}
            >
                <img
                    src={selectedImage}
                    alt="No image found"
                    style={{ width: '100%', maxWidth: '600px', height: 'auto', display: 'block', margin: '0 auto' }}
                />
            </Dialog>
        </div>
    );
};

export default POSPurchase;
