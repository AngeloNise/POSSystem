'use client';
import React, {useState, useRef, useEffect} from 'react';
import {Button} from 'primereact/button';
import {Column} from 'primereact/column';
import {DataTable} from 'primereact/datatable';
import {FilterMatchMode, FilterOperator} from 'primereact/api';
import {InputText} from 'primereact/inputtext';
import {InputNumber} from 'primereact/inputnumber';
import {Toast} from 'primereact/toast';
import {Checkbox} from "primereact/checkbox";
import {Dialog} from "primereact/dialog";
import { Calendar } from 'primereact/calendar';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';


import { format } from 'date-fns';

import useAuthStore from '@/store/AuthStore';
import ProductSerialNumberService from '@/service/access_management/ProductSerialNumberService'
import ProductService from '@/service/access_management/ProductService';
import {Image} from "primereact/image";
import {Dropdown} from "primereact/dropdown";
import {Paginator} from "primereact/paginator";
import {Ripple} from "primereact/ripple";
import {classNames} from "primereact/utils";
import {Accordion, AccordionTab} from "primereact/accordion";
import {serialize} from "v8";
import {MultiSelect} from "primereact/multiselect";
import {ProgressSpinner} from "primereact/progressspinner";


const ProductManagement = () => {
    const toast = useRef(null);

    const {userInfo} = useAuthStore();
    const productSerialService = new ProductSerialNumberService(userInfo);
    const productService = new ProductService(userInfo);
    const [selectedProductImage, setSelectedProductImage] = useState('');
    const [createdDate, setCreatedDate] = useState(null);
    const [modifiedDate, setModifiedDate] = useState(null)
    const [inputSerial, setInputSerial] = useState('');
    const [selectedSerials, setSelectedSerials] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState();
    const [searchSerial, setSearchSerial] = useState("");
    const [debounce, setDebounce] = useState("");
    const [productList, setProductList] = useState(null);
    const [product, setProduct] = useState(null);
    const [productSerialNUmberList, setProductSerialNumberList] = useState([]);

    const [displayProductImage, setDisplayProductImage] = useState(false)
    const [displaySerialModal, setDisplaySerialModal] = useState(false);
    const [displayBasic, setDisplayBasic] = useState(false);
    const [disabledStocksForm, setDisabledStocksForm] = useState(false);
    const [disabledForm, setDisabledForm] = useState(true);
    const [displayAddSerialDiv, setDisplayAddSerialDiv] = useState(false);
    const [loading, setLoading] = useState(false);
    const [loadingSignIn, setLoadinggSignIn] = useState(false);
    const [tableLoading, setTableLoading] = useState(false);
    const [searchLoading, setSearchLoading] = useState(false);

    const [actionType, setActionType] = useState("none");
    const [selectedType, setSelectedType] = useState('NO');
    const [productImage, setProductImage] = useState(null);
    const fileUpload = useRef(null);
    const [tableLimit, setTableLimit] = useState(10);
    const paginator = useRef(null);
    const [queryParams, setQueryParams] = useState(null);
    const [tableStart, setTableStart] = useState(0);
    const [tableTotalCount, setTableTotalCount] = useState(0);
    const [forClearPurposed, setForClearPurposed] = useState("none");
    const [serialStart, setSerialStart] = useState(0);
    const [serialLimit, setSerialLimit] = useState(10);

    const [messageBasedOn, setMessageBasedOn] = useState("");

    const [filters, setFilters] = useState({
        global: {value: null, matchMode: FilterMatchMode.CONTAINS}
    });


    const paginatorTemplate = {
        layout: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown CurrentPageReport',
        'PrevPageLink': (options) => {
            return (
                <button type="button" className={options.className} onClick={options.onClick}
                        disabled={options.disabled}>
                    <span className="p-3">Previous</span>
                    <Ripple/>
                </button>
            )
        },
        'NextPageLink': (options) => {
            return (
                <button type="button" className={options.className} onClick={options.onClick}
                        disabled={options.disabled}>
                    <span className="p-3">Next</span>
                    <Ripple/>
                </button>
            )
        },
        'PageLinks': (options) => {
            if ((options.view.startPage === options.page && options.view.startPage !== 0) || (options.view.endPage === options.page && options.page + 1 !== options.totalPages)) {
                const className = classNames(options.className, {'p-disabled': true});

                return <span className={className} style={{userSelect: 'none'}}>...</span>;
            }
            return (
                <button type="button" className={options.className} onClick={options.onClick}>
                    {options.page + 1}
                    <Ripple/>
                </button>
            )
        },
        'RowsPerPageDropdown': (options) => {
            const dropdownOptions = [
                {label: 10, value: 10},
                {label: 25, value: 25},
                {label: 50, value: 50},
                {label: 100, value: 100}
            ];
            return <Dropdown value={options.value} options={dropdownOptions} onChange={options.onChange}/>;
        },
        'CurrentPageReport': (options) => {
            return (
                <span style={{color: 'var(--text-color)', userSelect: 'none', width: '120px', textAlign: 'center'}}>
                    {options.first} - {options.last} of {options.totalRecords}
                </span>
            )
        }
    };

    /** POP UP MESSAGES **/

    const messageToast = (severity, detail)=>{
        toast.current.show({
            severity: severity,
            summary: 'Inventory page',
            detail: detail,
            life: 3000
        })
    }

    const accept = (productId,messageBasedOn) => {
        if(messageBasedOn === "product") {
            deleteProduct(productId)
        }else{
            deleteSerialNumbers(productId)
        }
    };
    const reject = () => {
    };

    const confirm = (messageConfirm, productId, messageBasedOn, event, fromMessage) => {
        confirmDialog({
            target: event.currentTarget,
            message: 'Are you sure you want to ' + messageConfirm + event + fromMessage + "?",
            icon: 'pi pi-exclamation-triangle',
            accept: () => accept(productId, messageBasedOn),
            reject: () => reject()
        });
    };

    /** LISTENERS **/


    const onClear = () => {
        setQueryParams('');
        setModifiedDate(null)
        setCreatedDate(null)
        setTableStart(0);
        setTableLimit(10);
        setActionType("none");
        setForClearPurposed("clear");
    };


    const handleSerializeBox = (value) => {
        if (value == "YES") {

            setDisplayAddSerialDiv(true);
            setDisabledStocksForm(true);
            setSelectedType("YES");
            setProduct({...product, stocks: null});

        } else {
            setDisabledStocksForm(false);
            setSelectedType("NO")
            setDisplayAddSerialDiv(false);
            setProduct({...product, stocks: null});
        }
    };


    const onAddSerial = () => {
        if (inputSerial.trim() !== '' && !selectedSerials.includes(inputSerial)) {
            setSelectedSerials([...selectedSerials, inputSerial.trim()]);
            setInputSerial('');
        }
    };



    const getSelectedProduct = (id, serialized) => {
        //check if id arg is not null
        if (serialized) {
            setSelectedType("YES");
            setDisabledStocksForm(true)

            //get the selected product via serial number
            const serialized = productList.filter(x => id == x.id);
            console.log("multi product filtered ", serialized[0])

            setProduct(serialized[0]);

        } else {
            //get the selected product not serialize via id arg
            const notSerialized = productList.filter(x => id == x.id);

            setProduct(notSerialized[0]);
            setSelectedType("NO");
            setDisabledStocksForm(false)

        }
    }

    const openDialog = (image) => {
        setSelectedProductImage(`/product-image/${image?.productImage || 'Placeholder.jpg'}`);
        setDisplayProductImage(true);
    };

    const onSelectImage = (event) => {

        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setProductImage({
                    image: reader.result,  // base64 string
                    file: file             // optional, useful if you need to upload it later
                });
                setProduct({...product, productImage: null});

            };
            reader.readAsDataURL(file); // convert file to base64
        }
    }

    const onDeleteImage = () => {
        setProductImage(null);
        fileUpload.current.value = ''; // clear file input
    };

    const handleShow = (productId) => {
        fetchItemSerialNumbers(productId);
        setDisplaySerialModal(true);

    };

    useEffect(() => {
        setTableLoading(true)
        selectAllProduct(tableStart, tableLimit);
    }, []);
    useEffect(() => {
        const timer = setTimeout(() => {
            setDebounce(searchSerial);
        }, 500);

        return () => clearTimeout(timer);
    }, [searchSerial]);


    useEffect(() => {
        if (debounce !== '') {
            fetchItemSerialNumbers(selectedProductId);
        } else {

        }
    }, [debounce]);


    /** APIs */

// for normal load page
    const selectAllProduct = async (tableStart, tableLimit) => {
        try {
            const response = await productService.onDisplayProducts(tableStart, tableLimit, queryParams, createdDate);
            setProductList(response.data);
            setTableTotalCount(response.totalCount);
            setTableLimit(response.pageSize);
            setTableStart(response.pageStart);
            setTableLoading(false);
            setLoadinggSignIn(false)
        } catch (err) {
            setTableLoading(false);
        }
    };

    // for paginator
    const onPageChange = async (e) => {
        try {
            setTableLoading(true);
            const response = await productService.onDisplayProducts(e.first, e.rows, queryParams, createdDate);
            setProductList(response.data);
            setTableTotalCount(response.totalCount);
            setTableLimit(response.pageSize);
            setTableStart(response.pageStart);
            setTableLoading(false);
        } catch (err) {
            console.error("Error loading products:", err);
            setTableLoading(false);
        }
    };


    //edit or add product
    const productsUpdate = async () => {
        try {
            const files = fileUpload.current.files;
            const response = await productService.productActionInfo(product, userInfo, selectedType, actionType, selectedSerials, files);
            selectAllProduct(tableStart, tableLimit);
            messageToast(response.severity, response.message);
        } catch (err) {
        }
    }

    const deleteProduct = async (productId) =>{
        try {
            const response = await productService.onDeleteProduct(productId);
            selectAllProduct(tableStart, tableLimit);
            messageToast(response.severity, response.message);
        }catch (err){

        }
    }

    const fetchItemSerialNumbers = async (productId) => {
        try {
            const response = await productSerialService.showSerialNumbers(serialStart, serialLimit, productId, debounce)
            setProductSerialNumberList(response.serialNumber);
        } catch (error) {

        }
    };


    const onAddSerialNumbers = async (productId) => {
        try{
            const response = await productSerialService.addSerialNumbers(productId, selectedSerials)
            messageToast(response.severity, response.message);
            fetchItemSerialNumbers(selectedProductId)
            setSelectedSerials([])
        }catch (err){

        }
    }

    const deleteSerialNumbers = async (productId) => {
        try {
            const response = await productSerialService.deleteSerialNumbers(productId, selectedSerials)
            messageToast(response.severity, response.message);
            fetchItemSerialNumbers(selectedProductId)
            setSelectedSerials([])
        }catch (err){
        }
    }


    /** LAYOUTS */
    const headerTable = () => {
        return <div>
            <div className="grid justify-content-between">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-start gap-2">
                        <Button label="Add" icon="pi pi-plus"
                                size="small" onClick={() => {
                            setDisplayBasic(true);
                            setDisabledForm(false);
                            setProduct({
                                createBy: userInfo?.ACCOUNT_NAME,
                                accountId: userInfo?.ACCOUNT_ID
                            });

                            setProductImage(null);
                            setActionType("add");
                            setSelectedType("NO")
                            setSelectedSerials([]);
                        }}/>

                    </div>
                </div>
                <div></div>
                <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                    <Button icon="pi pi-refresh" size="small" loading={loadingSignIn}  onClick={() => {
                        setTableLoading(true)
                        setLoadinggSignIn(true);
                        selectAllProduct(tableStart, tableLimit);}} />
                </div>
            </div>
        </div>
    }

    const actionBodyTemplate = (row) => {
        return (
            <div>
                <span>
                    <Button data-id={row.id} icon="pi pi-pencil"
                            className="p-button-outlined p-button-success mr-2"
                            tooltip="Edit" size="small"
                            onClick={(e) => {
                                setDisplayBasic(true);
                                const id = row.id;//element.id;
                                const serialized = row.serialize;
                                setDisplayAddSerialDiv(serialized);
                                getSelectedProduct(id, serialized);
                                setSelectedProductId(id)
                                setDisabledForm(false);
                                setActionType("edit");
                                setProductImage(null);
                                setSelectedSerials([]);
                            }}/>
                </span>

                <span>
                    <Button data-id={row.id} icon="pi pi-trash" className="p-button-outlined p-button-warning"
                            tooltip="Delete" size="small"
                            onClick={(e) => {
                                const productId = row.id;//element.id;
                                setSelectedProductId(productId)
                                const messageBasedOn = "product";
                                confirm("Delete ",productId, messageBasedOn, row.productName, " from product");
                            }}/>
                </span>
            </div>
        );
    }


    const productImageTemplate = (image) => {
        return (
            <div className=" flex justify-content-center">
                <Image
                    src={`/product-image/${image?.productImage || 'Placeholder.jpg'}`}
                    alt="Product Image"
                    width="70"
                    height="40"
                    className="w-6rem  "
                    onClick={ () => {
                        openDialog(image);
                    }}/>
            </div>
        );
    };


    const stockSerialNumbers = (rowData) => {
        const productId = rowData.id;
        const shouldDisplaySN = rowData.serialize;
        const stocks = rowData.stocks;

        return (
            <div className="flex justify-content-center align-items-center h-full">
                {shouldDisplaySN ? (
                    <Button
                        className="p-button-rounded p-button-text text-gray-600 bg-gray-100"
                        icon="pi pi-eye"
                        onClick={() => {
                            setSelectedSerials([]);
                            setSelectedProductId(productId);
                            handleShow(productId);
                            setDisplaySerialModal(true);
                        }}
                    />
                ) : (
                    <span>{stocks}</span>
                )}
            </div>
        );
    };


    const customHeaderSerialSearch = (

        <div className="p-2">
            <InputText
                placeholder="Search Serial..."
                className="w-full"
                type="text" id="inputProductName" value={searchSerial || ''} style={{width: '100%'}}
                onChange={e => setSearchSerial(e.target.value)}/>
        </div>
    );


    const footerDialogProductDetails = (
        <div>
            <Button type="button" label="Dismiss" size="small" text
                    onClick={() => setDisplayBasic(false)}/>

            <Button type="button" label="Save" className="p-button-primary" size="small"
                    disabled={disabledForm} onClick={() => {
                productsUpdate()
            }}/>
            {/**
             <Button type="button" label="Save" className="p-button-primary" size="small"
             disabled={disabledForm} onClick={() => {
             productsUpdate()
             setDisplayBasic(false);
             }}/>*/}

        </div>
    );


    const footerDialogViewSerialNumber = (
        <div>
            <Button type="button" label="Dismiss" size="small" text
                    onClick={() => {
                        setDisplaySerialModal(false);
                    }}/>

            <Button type="button" label="Save" className="p-button-primary" size="small"
                    onClick={() => {
                        onAddSerialNumbers(selectedProductId);

                    }}/>
            {/**
             <Button type="button" label="Save" className="p-button-primary" size="small"
             onClick={() => {
             onAddSerialNumbers(selectedProductId);

             setDisplaySerialModal(false);

             }}/>*/}

        </div>
    );

    return (
        <div className="grid">
            <Toast ref={toast}/>
            <ConfirmDialog />
            <div className="col-12">
                <Accordion>
                    <AccordionTab header="Filters">
                        <div className="p-fluid">
                            <div className="grid ">
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="fullName" value={queryParams?.serial_number || ''}
                                                   onChange={e => setQueryParams({
                                                       ...queryParams,
                                                       serial_number: e.target.value
                                                   })}/>
                                        <label htmlFor="fullName">SERIAL NUMBER</label>
                                    </span>
                                </div>

                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="fullName" value={queryParams?.name || ''}
                                                   onChange={e => setQueryParams({
                                                       ...queryParams,
                                                       name: e.target.value
                                                   })}/>
                                        <label htmlFor="fullName">PRODUCT NAME</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="variation" value={queryParams?.variation || ''}
                                                   onChange={e => setQueryParams({
                                                       ...queryParams,
                                                       variation: e.target.value
                                                   })}/>
                                        <label htmlFor="variation">VARIATION</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                         <InputText id="modifiedBy" value={queryParams?.modified_by || ''}
                                                    onChange={e => setQueryParams({
                                                        ...queryParams,
                                                        modified_by: e.target.value
                                                    })}/>
                                        <label htmlFor="modifiedBy">MODIFIED BY</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="fullName" value={queryParams?.created_by || ''}
                                                   onChange={e => setQueryParams({
                                                       ...queryParams,
                                                       created_by: e.target.value
                                                   })}/>
                                        <label htmlFor="fullName">CREATED BY</label>
                                    </span>
                                </div>

                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">

                                        <Calendar
                                            id="buttondisplay"
                                            value={createdDate}
                                            onChange={(e) => setCreatedDate(e.value)}
                                            showIcon
                                            dateFormat="dd/mm/yy"
                                        />
                                        <label htmlFor="buttondisplay">Created Date</label>
                                    </span>
                                </div>

                            </div>
                        </div>
                        <div className="flex justify-content-end gap-2">
                            <Button label="Clear" icon="pi pi-filter-slash" size="small" outlined onClick={onClear}/>
                            <Button label="Search" icon="pi pi-search" size="small" loading={searchLoading}
                                    onClick={() => {
                                        selectAllProduct(tableStart, tableLimit)
                                    }}/>
                        </div>
                    </AccordionTab>
                </Accordion>
            </div>

            <Toast ref={toast} position="center"/>
            <div className="col-12 md:col-12">
                <DataTable
                    id="id" value={productList} className="p-datatable-gridlines" showGridlines
                    rows={tableLimit}
                    header={headerTable} filters={filters} loading={tableLoading}
                    responsiveLayout="scroll"
                    emptyMessage="No product found"
                >
                    <Column header="Action" body={actionBodyTemplate} style={{minWidth: '9rem'}}/>
                    <Column field="productImage" header="Image" style={{minWidth: '10rem'}} body={productImageTemplate}/>
                    <Column field="productName" header="Name" style={{minWidth: '12rem'}}/>
                    <Column field="variation" header="Variation" style={{minWidth: '12rem'}}/>
                    <Column field="capital" header="Capital" style={{minWidth: '12rem'}}/>
                    <Column field="profit" header="Profit" style={{minWidth: '12rem'}}/>
                    <Column field="createBy" header="Created By" style={{minWidth: '12rem'}}/>
                    <Column field="createdDate" header="Created Date" style={{minWidth: '12rem'}}/>
                    <Column field="modifiedBy" header="Last Modified By" style={{minWidth: '12rem'}}/>
                    <Column field="modifiedDate" header="Last Modified Date" style={{minWidth: '12rem'}}/>
                    <Column header="Stocks/SN" className="flex justify-content-center align-content-center" body={stockSerialNumbers} style={{minWidth: '7rem'}}/>
                </DataTable>
                <Paginator ref={paginator} template={paginatorTemplate} first={tableStart} rows={tableLimit}
                           totalRecords={tableTotalCount}
                           onPageChange={(e) => onPageChange(e)}/>
            </div>

            {/* DIALOGS */}
            <Dialog header="Product Details" visible={displayBasic} className="account-deets" modal
                    footer={footerDialogProductDetails} onHide={() => setDisplayBasic(false)}
                    onShow={() => {

                    }}>
                <br/>
                <div style={{backgroundColor: "#fafafa", padding: '25px', borderRadius: '10px'}}>
                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputProductName" value={product?.productName || ''}
                                           disabled={disabledForm} style={{width: '100%'}}
                                           onChange={e => setProduct({...product, productName: e.target.value})}/>
                                <label htmlFor="inputProductName">Product Name <span
                                    style={{color: 'red'}}>*</span></label>
                            </span>
                        </div>

                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputVariation" value={product?.variation || ''}
                                           disabled={disabledForm} style={{width: '100%'}}
                                           onChange={e => setProduct({...product, variation: e.target.value})}/>
                                <label htmlFor="inputVariation">Variation</label>
                            </span>
                        </div>

                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputNumber type="text" id="inputCapital" value={product?.capital || ''}
                                             disabled={disabledForm} style={{width: '100%'}}
                                             onValueChange={e => setProduct({...product, capital: e.target.value})}/>
                                <label htmlFor="inputCapital">Capital</label>
                            </span>
                        </div>
                    </div>

                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputNumber type="text" id="inputProfit" value={product?.profit || ''}
                                             disabled={disabledForm} style={{width: '100%'}}
                                             onValueChange={e => setProduct({...product, profit: e.target.value})}/>
                                <label htmlFor="inputProfit">Profit</label>
                            </span>
                        </div>

                        <div className="field col-12 md:col-4">
                            <div>
                            <span className="p-float-label">
                                <InputText type="text" id="inputAccName" value={userInfo?.ACCOUNT_NAME || ''} disabled
                                           style={{width: '100%'}}
                                />
                                <label htmlFor="inputAccName">Create By:</label>
                            </span>
                            </div>
                        </div>

                        <div className="field col-12 md:col-4">
                            <span className="flex p-float-label">
                                <InputNumber type="text" id="inputStocks" value={product?.stocks || ''}
                                             disabled={disabledStocksForm} style={{width: '100%'}}
                                             onValueChange={e => setProduct({...product, stocks: e.target.value})}/>
                                <label htmlFor="inputStocks">Stocks</label>
                            </span>

                            <div className="mt-2">
                                <Checkbox
                                    inputId="Serialize"
                                    onChange={(e) => handleSerializeBox(e.checked ? 'YES' : 'NO')}
                                    checked={selectedType == "YES"}
                                />
                                <label htmlFor="serialize" className="ml-2">Serialize</label>
                            </div>
                        </div>

                    </div>

                    <div className="grid">
                        <div className="col-12 ">
                            <div className="field col-12 ">

                                {displayAddSerialDiv ? ( <>
                                        <div
                                            className="field col-12"
                                            style={{
                                                maxHeight: '100px',
                                                height: '100px',
                                                overflowX: 'auto', // horizontal scroll
                                                overflowY: 'hidden',
                                                border: '1px solid #ccc',
                                                padding: '8px',
                                                borderRadius: '5px',
                                                justifyContent: 'flex-start',
                                                display: 'flex',
                                                gap: '5px',
                                            }}
                                        >

                                            {selectedSerials.map((sn) => (
                                                <div
                                                    key={sn}
                                                    style={{
                                                        background: '#e3e3e3',
                                                        padding: '5px 10px',
                                                        height: '70px',
                                                        borderRadius: '12px',
                                                        display: 'inline-flex',
                                                        alignItems: 'center',
                                                        cursor: 'pointer',
                                                        whiteSpace: 'nowrap',
                                                    }}
                                                    onClick={() =>
                                                        setSelectedSerials(selectedSerials.filter((s) => s !== sn))
                                                    }
                                                >
                                                    {sn} &times;
                                                </div>
                                            ))}

                                        </div>
                                        <div className="grid mt-2">
                                            <div className="field col-12 md:col-4">
                                             <span className="p-float-label">
                                                   <InputText type="text"  id="inputSerial"
                                                              value={inputSerial}
                                                              onChange={(e) => setInputSerial(e.target.value)}
                                                              style={{ width: '100%' }}
                                                   />
                                        <label htmlFor="inputSerial">Add serial</label>
                                                 </span>
                                            </div>
                                            <Button  icon="pi pi-plus"
                                                     className="p-button-outlined p-button-success ml-0 h-3rem mt-2"
                                                     tooltip="Add serial"
                                                     size="small" onClick={onAddSerial} />
                                        </div></>
                                ) : (
                                    <div></div>)}


                            </div>
                        </div>
                    </div>



                    <div className="grid">
                        <div className="col-12 ">
                            <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <label htmlFor="dropdown">Product Image</label>
                            </span>
                                <div className="mt-4">
                                    {
                                        actionType === "add" || actionType === "edit"
                                            ? <input
                                                type="file"
                                                accept="image/*"
                                                onChange={onSelectImage}
                                                ref={fileUpload}
                                            /> : null
                                    }

                                    <div>
                                        <div className="flex flex-wrap justify-center w-[150px] h-[100px]">
                                            <Image
                                                preview
                                                src={
                                                    product?.productImage
                                                        ? `/universal-store-sakai/product-image/${product.productImage}`
                                                        : productImage?.image
                                                }
                                                className="mt-3"
                                                width="100%"
                                                height="100%"
                                            />
                                        </div>

                                        <div className="flex flex-wrap justify-content-center">
                                            {
                                                actionType === "add" || actionType === "edit"
                                                    ? <Button label="Remove"
                                                              className="p-button p-component p-button-text p-button-danger"
                                                              onClick={onDeleteImage}/> : null
                                            }
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Dialog>


            {/** DIALOG FOR PRODUCT SERIAL */}

            <Dialog header="Product serial" visible={displaySerialModal} className="account-deets col-7" modal
                    footer={footerDialogViewSerialNumber} onHide={() => {setDisplaySerialModal(false)}}
                    onShow={() => {
                    }}>
                <br/>
                <div style={{backgroundColor: "#fafafa", padding: '25px', borderRadius: '10px'}}>
                    <div className="flex align-content-center">
                        <div
                            className="field col-12 justify-content-between"
                            style={{
                                maxHeight: '100px',
                                height: '100px',
                                overflowX: 'auto', // horizontal scroll
                                overflowY: 'hidden',
                                border: '1px solid #ccc',
                                padding: '8px',
                                borderRadius: '5px',
                                display: 'flex',
                                gap: '5px',
                            }}
                        >
                            <div>

                                {selectedSerials.map((sn) => (
                                    <div
                                        key={sn}
                                        className="ml-1"
                                        style={{
                                            background: '#e3e3e3',
                                            padding: '5px 10px',
                                            height: '70px',
                                            borderRadius: '12px',
                                            display: 'inline-flex',
                                            alignItems: 'center',
                                            cursor: 'pointer',
                                            whiteSpace: 'nowrap',
                                        }}
                                        onClick={() =>
                                            setSelectedSerials(selectedSerials.filter((s) => s !== sn))
                                        }>
                                        {sn} &times;
                                    </div>
                                ))}
                            </div>

                            <div className="flex  justify-content-center " style={{width: "100", height: "18px"}} >
                                <div className="field " >
                                    <div>
                                 <span className="p-float-label">
                                      <Button
                                          className="mt-0 mr-1 p-button-text p-button-danger"
                                          icon="pi pi-trash"
                                          style={{width: "18px", height: "18px"}}
                                          onClick={() => {
                                              const messageBasedOn = "serial";
                                              confirm("Delete serial number",selectedProductId, messageBasedOn,  selectedSerials, " ");
                                          }}/>
                                  </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div className="flex justify-content-between">
                        <div className="field justify-content-between">
                            <MultiSelect
                                value={selectedSerials}
                                options={productSerialNUmberList
                                    .filter(sn => sn != null)
                                    .map(sn => ({
                                        label: String(sn),
                                        value: sn
                                    }))
                                }
                                onChange={(e) => setSelectedSerials(e.value)}
                                placeholder="Select Serial Numbers"
                                filter={false}
                                maxSelectedLabels={3} className="w-full md:w-20rem"
                                style={{width: '100%'}}
                                panelHeaderTemplate={customHeaderSerialSearch}
                                panelFooterTemplate={loading ? <ProgressSpinner style={{width: '20px', height: '20px'}}/> : null}/>
                        </div>

                        <div className="flex justify-content-between">
                            <div className="field ">
                                 <span className="p-float-label">
                                    <InputText type="text"  id="inputSerial"
                                               value={inputSerial}
                                               onChange={(e) => setInputSerial(e.target.value)}
                                    />
                                    <label htmlFor="inputSerial">Add serial</label>
                                 </span>
                            </div>
                            <Button  icon="pi pi-plus"
                                     className="p-button-outlined p-button-success mr-1 ml-1 h-3rem "
                                     tooltip="Add serial"
                                     size="small" onClick={onAddSerial} />
                        </div>
                    </div>
                </div>
            </Dialog>

            <Dialog
                header="Product Image"
                visible={displayProductImage}
                style={{ width: 'auto' }}
                modal
                onHide={() => setDisplayProductImage(false)}
            >
                <img
                    src={selectedProductImage}
                    alt="No image found"
                    style={{ width: '100%', maxWidth: '600px', height: 'auto', display: 'block', margin: '0 auto' }}
                />
            </Dialog>

        </div>
    );
};
export default ProductManagement;
