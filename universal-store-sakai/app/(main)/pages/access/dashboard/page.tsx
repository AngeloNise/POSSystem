'use client';
import React, { useState, useEffect, useRef } from 'react';

import ProductSalesService from "@/service/access_management/ProductSalesService";
import {Ripple} from "primereact/ripple";
import { Paginator } from 'primereact/paginator';
import { Toast } from 'primereact/toast';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Card } from 'primereact/card';
import {classNames} from "primereact/utils";
import {Dropdown} from "primereact/dropdown";
import {Button} from "primereact/button";
import {useRouter} from "next/navigation";
import {Dialog} from "primereact/dialog";
import StoreService from "@/service/access_management/StoreService";

const Dashboard = () => {
    const storeService = new StoreService();
    const productSalesService = new ProductSalesService();
    const router = useRouter();
    const toast = useRef(null);
    const dataTable = useRef(null);
    const paginator = useRef(null);
    const paginatorRecent = useRef(null);

    const [selectedPeriod, setSelectedPeriod] = useState('today');
    const [store, setStore] = useState({ storeName: "", storeLogo: "" });
    const [newName, setNewName] = useState("");
    const [newLogo, setNewLogo] = useState(null);
    const [previewUrl, setPreviewUrl] = useState("");
    const [companyName, setCompanyName] = useState('');
    const [tempName, setTempName] = useState(newName);
    const [isEditingLogo, setIsEditingLogo] = useState(false);
    const [isEditingName, setIsEditingName] = useState(false);
    const [productSaleLists, setProductSaleLists] = useState([]);
    const [tableStart, setTableStart] = useState(0);
    const [tableLimit, setTableLimit] = useState(10);
    const [tableTotalCount, setTableTotalCount] = useState(0);
    const [tableLoading, setTableLoading] = useState(false);
    const [searchLoading, setSearchLoading] = useState(false);
    const [refreshLoading, setRefreshLoading] = useState(false);

    const [visible, setVisible] = useState(false);
    const [recentSaleLists, setRecentSaleLists] = useState([]);
    const [tableStartRecent, setTableStartRecent] = useState(0);
    const [tableLimitRecent, setTableLimitRecent] = useState(10);
    const [tableTotalCountRecent, setTableTotalCountRecent] = useState(0);
    const [tableLoadingRecent, setTableLoadingRecent] = useState(false);
    const [searchLoadingRecent, setSearchLoadingRecent] = useState(false);
    const [refreshLoadingRecent, setRefreshLoadingRecent] = useState(false);
    const [selectedImage, setSelectedImage] = useState(null);

    const [metrics, setMetrics] = useState({
        capital: 0,
        profit: 0,
        earnings: 0
    });

    const [queryParams, setQueryParams] = useState({
        "image": "",
        "name": "",
        "serialize":""
    });

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

    useEffect(() => {
        queryProductSaleList(tableStart, tableLimit, false, false);
    }, [])

    const queryProductSaleList = (start, limit, refresh, search) => {
        setTableLoading(true);
        if (search) setSearchLoading(true);
        if (refresh) setRefreshLoading(true);

        const params = {
            image: queryParams.image,
            name: queryParams.name,
            start: start,
            limit: limit,
        };

        productSalesService.dashboardList(params)
            .then((res) => {
                setProductSaleLists(res.data.data);
                setTableStart(start);
                setTableTotalCount(res.data.totalCount);
                setTableLoading(false);
                setSearchLoading(false);
                setRefreshLoading(false);
            })
            .catch((error) => {
                if (error.response && error.response.status === 401) {
                    router.push("/auth/login");
                } else {
                    toast.current.show({ severity: 'error', detail: "Error getting out of stock products", life: 3000 });
                    setTableLoading(false);
                    setSearchLoading(false);
                    setRefreshLoading(false);
                }
            });
    };

    const onPageChange = (e) => {
        setTableStart(e.first);
        setTableLimit(e.rows);
        queryProductSaleList(e.first, e.rows, false, false);
    }

    const onClickRefresh = () => {
        queryProductSaleList(0, tableLimit, true, false);
    }

    const headerTable = () => {
        return <div>
            <div className="grid">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                    </div>
                </div>
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                        <Button icon="pi pi-refresh" onClick={onClickRefresh} size="small" loading={refreshLoading} />
                    </div>
                </div>
            </div>
        </div>
    }

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

    //recent purchase list
    const [queryParamsRecent, setQueryParamsRecent] = useState({
        "productName": "",
        "counter": "",
        "createdBy":"",
        "createdDate": ""
    });

    const paginatorTemplateRecent = {
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

    useEffect(() => {
        queryRecentSaleList(tableStartRecent, tableLimitRecent, false, false);
    }, [])

    const queryRecentSaleList = (start, limit, refresh, search) => {
        setTableLoadingRecent(true);
        if (search) setSearchLoadingRecent(true);
        if (refresh) setRefreshLoadingRecent(true);

        const params = {
            productName: queryParamsRecent.productName,
            counter: queryParamsRecent.counter,
            createdBy: queryParamsRecent.createdBy,
            createdDate: queryParamsRecent.createdDate,
            start: start,
            limit: limit,
        };

        if (
            !params.productName &&
            !params.counter &&
            !params.createdBy &&
            !params.createdDate
        ) {
            const today = new Date().toISOString().split('T')[0];
            params.createdDate = today;
        }

        productSalesService.recentList(params)
            .then((res) => {
                setRecentSaleLists(res.data.data);
                setTableStartRecent(start);
                setTableTotalCountRecent(res.data.totalCount);
                setTableLoadingRecent(false);
                setSearchLoadingRecent(false);
                setRefreshLoadingRecent(false);
            })
            .catch((error) => {
                if (error.response && error.response.status === 401) {
                    router.push("/auth/login");
                } else {
                    toast.current.show({ severity: 'error', detail: "Error getting out of recent sale", life: 3000 });
                    setTableLoadingRecent(false);
                    setSearchLoadingRecent(false);
                    setRefreshLoadingRecent(false);
                }
            });
    };

    const onPageChangeRecent = (e) => {
        setTableStartRecent(e.first);
        setTableLimitRecent(e.rows);
        queryRecentSaleList(e.first, e.rows, false, false);
    }

    const onClickRefreshRecent = () => {
        queryRecentSaleList(0, tableLimit, true, false);
    }

    const headerTableRecent = () => {
        return <div>
            <div className="grid">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                    </div>
                </div>
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                        <Button icon="pi pi-refresh" onClick={onClickRefreshRecent} size="small" loading={refreshLoadingRecent} />
                    </div>
                </div>
            </div>
        </div>
    }

    useEffect(() => {
        // Fetch store info on mount
        storeService.getStore().then(res => {
            setStore(res.data);
            setNewName(res.data.storeName || "");
            setPreviewUrl(`/store-image/${res.data.storeLogo}`);
        }).catch(console.error);
    }, []);

    const onFileChange = e => {
        const file = e.target.files[0];
        setNewLogo(file);
        setPreviewUrl(URL.createObjectURL(file));
    };

    const onSave = () => {
        const formData = new FormData();
        formData.append('store', new Blob([JSON.stringify({ storeName: newName })], { type: 'application/json' }));

        if (newLogo) {
            formData.append('logoFile', newLogo);
        }

        storeService.axiosInstance.post(`${storeService.baseApi()}/update`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        }).then(res => {
            toast.current.show({
                severity: 'success',
                summary: 'Success',
                detail: 'Store updated successfully!',
                life: 3000,
                icon: 'pi pi-check-circle'
            });
            setStore(res.data);
            setPreviewUrl(`/store-image/${res.data.storeLogo}`);
            setNewLogo(null);
        }).catch(err => {
            toast.current.show({
                severity: 'error',
                summary: 'Error',
                detail: 'Update failed!',
                life: 3000,
                icon: 'pi pi-exclamation-triangle'
            });
            console.error(err);
        });
    };

    const periodOptions = [
        { label: 'Today', value: 'today' },
        { label: 'Weekly', value: 'week' },
        { label: 'Monthly', value: 'month' },
        { label: 'Quarterly', value: 'quarter' },
        { label: 'Annually', value: 'year' }
    ];

    const fetchMetrics = (period) => {
        productSalesService.getMetricsByPeriod(period)
            .then(response => {
                setMetrics({
                    capital: response.data.capital,
                    profit: response.data.profit,
                    earnings: response.data.earnings || response.data.price
                });
            })
            .catch(error => {
                console.error(`Error fetching ${period} metrics:`, error);
            });
    };

    useEffect(() => {
        fetchMetrics(selectedPeriod);
    }, [selectedPeriod]);

    return (

        <div className="grid">
            <Toast ref={toast} />
            <div className="col-12 lg:col-12 xl:col-12">
                <div className="grid">

                    <div className="col-12 lg:col-6">
                        <Card className="store-edit-card p-shadow-3 p-mb-3 p-p-3 rounded-lg max-w-sm mx-auto">
                            <h2 className="text-xl font-semibold mb-4 text-center">Edit Store Logo</h2>

                            <div className="text-center">
                                {previewUrl && (
                                    <div className="relative inline-block -mt-4">
                                        <img
                                            src={previewUrl}
                                            alt="Store Logo Preview"
                                            className="rounded-lg object-contain shadow-sm"
                                            style={{ width: '30%' }}
                                        />
                                        <button
                                            type="button"
                                            className="absolute top-1 right-1 p-button p-button-text p-button-sm"
                                            aria-label="Edit Store Logo"
                                            onClick={() => setIsEditingLogo(true)}
                                        >
                                            <i className="pi pi-pencil" />
                                        </button>
                                    </div>
                                )}
                            </div>

                            {isEditingLogo && (
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={e => {
                                        onFileChange(e);
                                        setIsEditingLogo(false);
                                    }}
                                    className="block mt-2"
                                />
                            )}
                        </Card>
                    </div>

                    <div className="col-12 lg:col-6">
                        <Card className="store-edit-card p-shadow-3 p-mb-4 p-p-4 rounded-lg max-w-md mx-auto">
                            <h2 className="text-2xl font-bold mb-6 text-center">Edit Store Name</h2>

                            <div className="flex items-center">
                                <label htmlFor="storeName" className="block font-semibold mr-4 mb-0 text-xl" style={{ minWidth: '110px' }}>
                                    Store Name:
                                </label>

                                {!isEditingName ? (
                                    <>
                                        <span className="text-2xl">{newName}</span>
                                        <button
                                            type="button"
                                            className="ml-3 p-button p-button-text p-button-sm"
                                            aria-label="Edit Store Name"
                                            onClick={() => {
                                                setTempName(newName);
                                                setIsEditingName(true);
                                            }}
                                        >
                                            <i className="pi pi-pencil" />
                                        </button>
                                    </>
                                ) : (
                                    <>
                                        <input
                                            id="storeName"
                                            type="text"
                                            value={tempName}
                                            onChange={e => setTempName(e.target.value)}
                                            className="p-inputtext p-component p-filled p-inputtext-sm w-auto max-w-xs text-xl"
                                            autoFocus
                                        />
                                        <button
                                            type="button"
                                            className="ml-2 p-button p-button-success p-button-sm"
                                            onClick={() => {
                                                setNewName(tempName);
                                                setIsEditingName(false);
                                            }}
                                        >
                                            Save
                                        </button>
                                        <button
                                            type="button"
                                            className="ml-2 p-button p-button-secondary p-button-sm"
                                            onClick={() => setIsEditingName(false)}
                                        >
                                            Cancel
                                        </button>
                                    </>
                                )}
                            </div>
                        </Card>
                    </div>

                </div>

                {/* Save button below both cards */}
                <div className="col-6 md:col-6">
                    <button
                        onClick={onSave}
                        className="p-button p-button-primary"
                        style={{ width: 'auto' }}
                    >
                        Save
                    </button>
                </div>
            </div>

            <div className="col-12 mb-3">
                <div className="flex justify-content-end">
                    <Dropdown
                        value={selectedPeriod}
                        options={periodOptions}
                        onChange={(e) => setSelectedPeriod(e.value)}
                        placeholder="Select Period"
                        className="w-full md:w-14rem"
                    />
                </div>
            </div>

            {/* Metrics Cards */}
            <div className="col-12 lg:col-6 xl:col-4">
                <div className="card mb-0">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">{periodOptions.find(p => p.value === selectedPeriod)?.label} Capital</span>
                            <div className="text-900 font-medium text-xl">{metrics.capital}</div>
                        </div>
                        <div className="flex align-items-center justify-content-center bg-blue-100 border-round" style={{ width: '2.5rem', height: '2.5rem' }}>
                            <i className="pi pi-wallet text-blue-500 text-xl" />
                        </div>
                    </div>
                </div>
            </div>

            <div className="col-12 lg:col-6 xl:col-4">
                <div className="card mb-0">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">{periodOptions.find(p => p.value === selectedPeriod)?.label} Profit</span>
                            <div className="text-900 font-medium text-xl">{metrics.profit}</div>
                        </div>
                        <div className="flex align-items-center justify-content-center bg-orange-100 border-round" style={{ width: '2.5rem', height: '2.5rem' }}>
                            <i className="pi pi-chart-line text-green-500 text-xl" />
                        </div>
                    </div>
                </div>
            </div>

            <div className="col-12 lg:col-6 xl:col-4">
                <div className="card mb-0">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">{periodOptions.find(p => p.value === selectedPeriod)?.label} Earnings</span>
                            <div className="text-900 font-medium text-xl">{metrics.earnings}</div>
                        </div>
                        <div className="flex align-items-center justify-content-center bg-cyan-100 border-round" style={{ width: '2.5rem', height: '2.5rem' }}>
                            <i className="pi pi-dollar text-green-500 text-xl" />
                        </div>
                    </div>
                </div>
            </div>

            <div className="col-12 xl:col-6">
                <Card className="card">
                    <h5>List of Out Of Stock</h5>
                    <DataTable
                        ref={dataTable}
                        dataKey="id"
                        value={productSaleLists}
                        rows={tableLimit}
                        emptyMessage="No data"
                        loading={tableLoading}
                        header={headerTable}
                        showGridlines
                    >
                        <Column
                            field="image"
                            header="Image"
                            style={{ minWidth: '10rem' }}
                            body={stockImageTemplate}
                            bodyStyle={{ padding: '1rem', textAlign: 'left' }}
                        />
                        <Column
                            field="name"
                            header="Product Name"
                            headerStyle={{ minWidth: '20rem', textAlign: 'center' }}
                        />
                    </DataTable>
                    <Paginator
                        ref={paginator}
                        template={paginatorTemplate}
                        first={tableStart}
                        rows={tableLimit}
                        totalRecords={tableTotalCount}
                        onPageChange={(e) => onPageChange(e)}
                    />
                </Card>
            </div>

            <div className="col-12 xl:col-6">
                <Card className="card">
                    <h5>List of Recent Sales</h5>
                    <DataTable
                        ref={dataTable}
                        dataKey="id"
                        value={recentSaleLists}
                        rows={tableLimit}
                        emptyMessage="No data"
                        loading={tableLoadingRecent}
                        header={headerTableRecent}
                        showGridlines
                    >
                        <Column
                            field="productName"
                            header="Product Name"
                            style={{ minWidth: '10rem' }}
                        />
                        <Column
                            field="counter"
                            header="Counter"
                            style={{ minWidth: '10rem' }}
                        />
                        <Column
                            field="createdBy"
                            header="Created By"
                            style={{ minWidth: '10rem' }}
                        />
                    </DataTable>
                    <Paginator
                        ref={paginatorRecent}
                        template={paginatorTemplateRecent}
                        first={tableStartRecent}
                        rows={tableLimitRecent}
                        totalRecords={tableTotalCountRecent}
                        onPageChange={(e) => onPageChangeRecent(e)}
                    />
                </Card>
            </div>

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

export default Dashboard;
