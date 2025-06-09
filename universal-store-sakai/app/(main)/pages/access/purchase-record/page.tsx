'use client';
import React, { useState, useEffect, useRef } from 'react';
import { InputText } from 'primereact/inputtext';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Card } from 'primereact/card';
import { Accordion, AccordionTab } from 'primereact/accordion';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ConfirmDialog } from 'primereact/confirmdialog';
import { Paginator } from 'primereact/paginator';
import { Ripple } from 'primereact/ripple';
import { classNames } from 'primereact/utils';
import { Dropdown } from 'primereact/dropdown';
import { useRouter } from 'next/navigation';
import { Calendar } from 'primereact/calendar';
import { format } from 'date-fns';
import PurchaseHistoryService from "@/service/access_management/PurchaseHistoryService";

const PurchaseRecord = () => {
    const purchaseHistoryService = new PurchaseHistoryService()
    const dataTable = useRef(null);
    const paginator = useRef(null);
    const toast = useRef(null);
    const today = new Date().toISOString().split('T')[0];
    const router = useRouter();

    const [tableStart, setTableStart] = useState(0);
    const [tableLimit, setTableLimit] = useState(10);
    const [tableTotalCount, setTableTotalCount] = useState(0);
    const [tableLoading, setTableLoading] = useState(false);
    const [searchLoading, setSearchLoading] = useState(false);
    const [refreshLoading, setRefreshLoading] = useState(false);
    const [purchaseRecords, setPurchaseRecords] = useState([]);
    const [visibleConfirmDelete, setVisibleConfirmDelete] = useState(false);
    const [deleteItemId, setDeleteItemId] = useState(null);

    const [queryParams, setQueryParams] = useState({
        "productName": "",
        "receiptNumber": "",
        "counter": "",
        "createdBy": "",
        "createdDate": ""
    });

    const noFiltersApplied = !queryParams.productName && !queryParams.receiptNumber && !queryParams.counter && !queryParams.createdBy && !queryParams.createdDate;

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
        queryPurchaseRecordList(tableStart, tableLimit, false, false);
    }, [])

    const queryPurchaseRecordList = (start, limit, refresh, search) => {
        setTableLoading(true);
        if (search) setSearchLoading(true);
        if (refresh) setRefreshLoading(true);

        const params = {
            productName: queryParams.productName,
            receiptNumber: queryParams.receiptNumber,
            counter: queryParams.counter,
            createdBy: queryParams.createdBy,
            createdDate: queryParams.createdDate,
            start: start,
            limit: limit
        };

        if (
            !params.productName &&
            !params.receiptNumber &&
            !params.counter &&
            !params.createdBy &&
            !params.createdDate // <- this is the key fix
        ) {
            const today = new Date().toISOString().split('T')[0];
            params.createdDate = today;
        }

        purchaseHistoryService.list(params)
            .then((res) => {
                setPurchaseRecords(res.data.data);
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
                    toast.current.show({ severity: 'error', detail: "Error getting Purchase Records", life: 3000 });
                    setTableLoading(false);
                    setSearchLoading(false);
                    setRefreshLoading(false);
                }
            });
    };

    const onPageChange = (e) => {
        setTableStart(e.first);
        setTableLimit(e.rows);
        queryPurchaseRecordList(e.first, e.rows, false, false);
    }

    const onChangeQueryParam = (e) => {
        const name = e.target.id;
        let value = e.target.value;

        if (name === 'createdDate') {
            // Remove all letters; allow only numbers and special characters
            value = value.replace(/[a-zA-Z]/g, '');
        }

        setQueryParams({ ...queryParams, [name]: value });
    };

    const onClickSearch = () => {
        queryPurchaseRecordList(0, tableLimit, false, true);
        setActiveFilters({ createdDate: queryParams.createdDate });
    }

    const [activeFilters, setActiveFilters] = useState({
        createdDate: today,
    });

    const onClickClear = () => {
        const clearedFilters = {
            productName: "",
            receiptNumber: "",
            counter: "",
            createdBy: "",
            createdDate: ""
        };
        setQueryParams(clearedFilters);
        setActiveFilters({ createdDate: today });
    };

    const onClickRefresh = () => {
        queryPurchaseRecordList(0, tableLimit, true, false);
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

    const onClickOpenReceiptPage = (receiptNumber) => {
        if (receiptNumber) {
            const params = new URLSearchParams({ receiptNumber: receiptNumber.toString() }).toString();
            window.open(`${window.location.origin}/pages/receipt?${params}`, "_blank");
        } else {
            window.open(`${window.location.origin}/pages/receipt`, "_blank");
        }
    };

    const deleteReceiptItem = (receiptItemId) => {
        purchaseHistoryService.delete(receiptItemId)  // Call the `delete` method from the service
            .then((res) => {
                toast.current.show({ severity: 'success', detail: 'Record deleted successfully', life: 3000 });
                queryPurchaseRecordList(tableStart, tableLimit, false, false);  // Refresh the list after deletion
            })
            .catch((error) => {
                toast.current.show({ severity: 'error', detail: 'Error deleting the record', life: 3000 });
            });
    };

    const actionColumnLayout = (rowData) => {
        return (
            <div className="flex gap-2">
                <Button
                    type="button"
                    icon="pi pi-eye"
                    className="p-button-rounded p-button-text"
                    tooltip="View in Receipt Page"
                    onClick={() => onClickOpenReceiptPage(rowData.receiptNumber)}
                    tooltipOptions={{ position: 'left' }}>
                </Button>
                <Button
                    type="button"
                    icon="pi pi-trash"
                    className="p-button-rounded p-button-danger p-button-text"
                    tooltip="Delete"
                    tooltipOptions={{ position: 'left' }}
                    onClick={() => { setDeleteItemId(rowData.id); setVisibleConfirmDelete(true); }}>
                </Button>
            </div>
        );
    };

    const onConfirmDelete = () => {
        deleteReceiptItem(deleteItemId);
        setVisibleConfirmDelete(false); // Close the dialog after deletion
    };

    return (
        <div className="grid">
            <Toast ref={toast} />
            <ConfirmDialog
                visible={visibleConfirmDelete}
                onHide={() => setVisibleConfirmDelete(false)}
                message="Are you sure you want to delete this record?"
                header="Delete Confirmation"
                icon="pi pi-exclamation-triangle"
                accept={onConfirmDelete}
                reject={() => setVisibleConfirmDelete(false)}
            />

            <div className="col-12">
                <Accordion>
                    <AccordionTab header="Filters">
                        <div className="p-fluid">
                            <div className="grid mt-2">
                                <div className="field col-4">
                                    <span className="p-float-label">
                                        <InputText id="productName" value={queryParams.productName} onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="productName">PRODUCT NAME</label>
                                    </span>
                                </div>
                                <div className="field col-4">
                                    <span className="p-float-label">
                                        <InputText id="receiptNumber" value={queryParams.receiptNumber} onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="receiptNumber">RECEIPT NUMBER</label>
                                    </span>
                                </div>
                                <div className="field col-4">
                                    <span className="p-float-label">
                                        <InputText id="counter" value={queryParams.counter} onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="counter">COUNTER</label>
                                    </span>
                                </div>
                                <div className="field col-4">
                                    <span className="p-float-label">
                                        <InputText id="createdBy" value={queryParams.createdBy} onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="createdBy">CREATED BY</label>
                                    </span>
                                </div>
                                <div className="field col-4">
                                    <span className="p-float-label">
                                        <Calendar
                                            id="createdDate"
                                            value={queryParams.createdDate ? new Date(queryParams.createdDate) : null}
                                            onChange={(e) => {
                                                const formattedDate = e.value ? format(e.value, 'yyyy-MM-dd') : '';
                                                onChangeQueryParam({ target: { id: 'createdDate', value: formattedDate } });
                                            }}
                                            onInput={(e) => {
                                                const input = e.target as HTMLInputElement;
                                                input.value = input.value.replace(/[a-zA-Z]/g, '');
                                            }}
                                            dateFormat="yy-mm-dd"
                                            showIcon
                                        />
                                        <label htmlFor="createdDate">CREATED DATE</label>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                            <Button label="Clear" icon="pi pi-filter-slash" size="small" onClick={onClickClear} outlined />
                            <Button label="Search" icon="pi pi-search" size="small" onClick={onClickSearch} loading={searchLoading} />
                        </div>
                    </AccordionTab>
                </Accordion>
            </div>

            <div className="field col-12">
                <Card className="p-2">
                    <h2 className="text-xl font-bold mb-4">
                        Purchase Records{activeFilters.createdDate && ` - ${activeFilters.createdDate}`}
                    </h2>

                    <DataTable ref={dataTable} dataKey="id" value={purchaseRecords} rows={tableLimit}
                               emptyMessage="No data" loading={tableLoading} header={headerTable} showGridlines>
                        <Column field="productName" header="Product Name" headerStyle={{ minWidth: '20rem', textAlign: 'center' }} />
                        <Column field="receiptNumber" header="Receipt Number" headerStyle={{ minWidth: '15rem', textAlign: 'center' }} />
                        <Column field="counter" header="Counter" headerStyle={{ minWidth: '10rem', textAlign: 'center' }} />
                        <Column field="createdBy" header="Created By" headerStyle={{ minWidth: '12rem', textAlign: 'center' }} />
                        <Column field="createdDate" header="Created Date" body={(rowData) => new Date(rowData.createdDate).toLocaleDateString('en-CA')} headerStyle={{ minWidth: '12rem', textAlign: 'center' }} />
                        <Column header="Action" body={actionColumnLayout} headerStyle={{ minWidth: '8rem', textAlign: 'center' }} />
                    </DataTable>
                    <Paginator ref={paginator} template={paginatorTemplate} first={tableStart} rows={tableLimit} totalRecords={tableTotalCount}
                               onPageChange={(e) => onPageChange(e)} />
                </Card>
            </div>
        </div>
    );
};

export default PurchaseRecord;
