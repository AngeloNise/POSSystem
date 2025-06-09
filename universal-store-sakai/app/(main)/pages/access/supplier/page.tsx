'use client';
import React, { useState, useRef, useEffect } from 'react';
import { Accordion, AccordionTab } from 'primereact/accordion';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { Calendar } from 'primereact/calendar';
import { format } from 'date-fns';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { FileUpload } from 'primereact/fileupload';
import { InputText } from 'primereact/inputtext';
import { Paginator } from 'primereact/paginator';
import { Ripple } from 'primereact/ripple';
import { Toast } from 'primereact/toast';
import { classNames } from 'primereact/utils';

import useAxiosInstance from '@/util/CustomAxios'
import useAuthStore from '@/store/AuthStore';
import { useRouter } from 'next/navigation';
import SupplierService from "@/service/access_management/SupplierService";


const Supplier = () => {
    const supplierService = new SupplierService();
    const dataTable = useRef(null);
    const paginator = useRef(null);
    const toast = useRef(null);
    const fileUploadRef = useRef(null);

    const axiosInstance = useAxiosInstance();
    const { auth } = useAuthStore();
    const router = useRouter();
    const [tableStart, setTableStart] = useState(0);
    const [tableLimit, setTableLimit] = useState(10);
    const [tableTotalCount, setTableTotalCount] = useState(0);
    const [tableLoading, setTableLoading] = useState(false);
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [refreshLoading, setRefreshLoading] = useState(false);
    const [saveLoading, setSaveLoading] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);
    const [downloadTemplateLoading, setDownloadTemplateLoading] = useState(false);
    const [exportLoading, setExportLoading] = useState(false);

    const [visibleAddDialog, setVisibleAddDialog] = useState(false);
    const [visibleImportDialog, setVisibleImportDialog] = useState(false);
    const [visibleConfirmSingleDeleteDialog, setVisibleConfirmSingleDeleteDialog] = useState(false);
    const [visibleConfirmMultipleDeleteDialog, setVisibleConfirmMultipleDeleteDialog] = useState(false);
    const [suppliers, setSuppliers] = useState([]);
    const [selectedImage, setSelectedImage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [selectedSupplier, setSelectedSupplier] = useState(null);
    const [selectedSuppliers, setSelectedSuppliers] = useState(null);

    const onFileSelect = (e) => {
        setSelectedFiles(e.files);
    };
    type SupplierState = {
        lastName: string;
        firstName: string;
        middleName: string;
        birthday: string;
        address: string;
        mobileNumber: string;
        telephoneNumber: string;
        email: string;
        facebook: string;
        instagram: string;
        image: string | File;
        motherName: string;
        fatherName: string;
    };

    const [localSupplier, setLocalSupplier] = useState<SupplierState>({
        lastName: "",
        firstName: "",
        middleName: "",
        birthday: "",
        address: "",
        mobileNumber: "",
        telephoneNumber: "",
        email: "",
        facebook: "",
        instagram: "",
        image: "",
        motherName: "",
        fatherName: ""
    });

    const [queryParams, setQueryParams] = useState({
        "lastName": "",
        "firstName": "",
        "middleName": "",
        "birthday": "",
        "address": "",
        "mobileNumber": "",
        "telephoneNumber": "",
        "email": "",
        "facebook": "",
        "instagram": "",
        "image": "",
        "motherName": "",
        "fatherName": ""
    });

    const [action, setAction] = useState(null);

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
        querySupplierList(tableStart, tableLimit, false, false);
    }, [])

    const baseApi = () => {
        return "/api/supplier";
    }

    const querySupplierList = (start, limit, refresh, search) => {
        setTableLoading(true);
        if (search) setSearchLoading(true);
        if (refresh) setRefreshLoading(true);
        var params = {
            "lastName": queryParams.lastName,
            "firstName": queryParams.firstName,
            "middleName": queryParams.middleName,
            "birthday": queryParams.birthday,
            "address": queryParams.address,
            "mobileNumber": queryParams.mobileNumber,
            "telephoneNumber": queryParams.telephoneNumber,
            "email": queryParams.email,
            "facebook": queryParams.facebook,
            "instagram": queryParams.instagram,
            "image": queryParams.image,
            "motherName": queryParams.motherName,
            "fatherName": queryParams.fatherName,
            "start": start,
            "limit": limit
        };
        supplierService.list(params)
            .then((res) => {
                setSuppliers(res.data.data);
                setTableStart(start);
                setTableTotalCount(res.data.totalCount);
                setTableLoading(false);
                setSearchLoading(false);
                setRefreshLoading(false);
            })
            .catch((error) => {
                if (error.status === 401) {
                    router.push("/auth/login");
                } else {
                    toast.current.show({ severity: 'error', detail: "Error getting suppliers", life: 3000 });
                    setTableLoading(false);
                    setSearchLoading(false);
                    setRefreshLoading(false);
                }
            });
    }

    const saveSupplier = () => {
        setSaveLoading(true);
        const endPoint = action === "CREATE" ? "/create" : "/update";

        const formData = new FormData();
        formData.append("supplier", new Blob([JSON.stringify(localSupplier)], { type: "application/json" }));

        // ✅ Only use selectedFiles (avoid .files from ref)
        if (selectedFiles.length > 0) {
            formData.append("image", selectedFiles[0]);
        }

        axiosInstance.post(baseApi() + endPoint, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        })
            .then((res) => {
                setSaveLoading(false);
                if (res.data.success) {
                    clearSupplier();
                    setVisibleAddDialog(false);
                    if (action === "CREATE") {
                        onClickRefresh();
                    } else {
                        querySupplierList(tableStart, tableLimit, true, false);
                    }
                    toast.current.show({ severity: 'success', detail: res.data.message, life: 3000 });
                } else {
                    toast.current.show({ severity: 'error', detail: res.data.message, life: 3000 });
                }
            })
            .catch(() => setSaveLoading(false));
    };


    const onPageChange = (e) => {
        setTableStart(e.first);
        setTableLimit(e.rows);
        querySupplierList(e.first, e.rows, false, false);
    }

    const onChangeSupplier = (e) => {
        const name = e.target.id;
        let value = e.target.value;

        if (name === 'birthday' || name === 'mobileNumber' || name === 'telephoneNumber') {
            // Remove all letters; allow only numbers and special characters
            value = value.replace(/[a-zA-Z]/g, '');
        }

        setLocalSupplier({ ...localSupplier, [name]: value });
    };

    const onChangeQueryParam = (e) => {
        const name = e.target.id; // e.g., 'mobileNumber'
        let value = e.target.value;

        if (name === 'mobileNumber') {
            // Remove all letters, allow only numbers and special characters
            value = value.replace(/[a-zA-Z]/g, '');
        }

        setQueryParams((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const onClickSearch = () => {
        querySupplierList(0, tableLimit, false, true);
    }

    const onClickClear = () => {
        setQueryParams({
            "lastName": "",
            "firstName": "",
            "middleName": "",
            "birthday": "",
            "address": "",
            "mobileNumber": "",
            "telephoneNumber": "",
            "email": "",
            "facebook": "",
            "instagram": "",
            "image": "",
            "motherName": "",
            "fatherName": ""
        });
    }

    const onClickRefresh = () => {
        querySupplierList(0, tableLimit, true, false);
    }

    const onClickAdd = () => {
        setAction("CREATE");
        clearSupplier();
        setVisibleAddDialog(true);
    }

    const onClickSaveSupplier = () => {
        saveSupplier();
    }

    const onUploadSuppliers = (event) => {
        const file = event.files[0]; // Get the file selected by the supplier
        supplierService.import(file) // Call the import method in SupplierService to send the file
            .then((response) => {
                if (response.data.success) {
                    setVisibleImportDialog(false);
                    querySupplierList(0, tableLimit, false, false);  // Refresh the supplier list
                    toast.current.show({ severity: 'success', detail: response.data.message, life: 3000 });
                } else {
                    toast.current.show({ severity: 'error', detail: response.data.message, life: 3000 });
                }
            })
            .catch((error) => {
                toast.current.show({ severity: 'error', detail: 'Error importing suppliers', life: 3000 });
            });
    };

    const onClickEdit = (localSupplier) => {
        setAction("UPDATE");
        setLocalSupplier(localSupplier);
        setVisibleAddDialog(true);
    }

    const onClickSingleDelete = (localSupplier) => {
        setSelectedSupplier(localSupplier);
        setVisibleConfirmSingleDeleteDialog(true);
    }

    const onAcceptSingleDelete = () => {
        var data = new FormData();
        data.append("id", selectedSupplier.id);
        axiosInstance.post(baseApi() + "/delete", data)
            .then((res) => {
                setVisibleConfirmSingleDeleteDialog(false);
                querySupplierList(0, tableLimit, false, false);
                toast.current.show({severity:'success', detail: res.data.message, life: 3000});
                setSelectedSupplier(null);
            })
            .catch((error) => {
                toast.current.show({severity:'error', detail: "Error deleting supplier", life: 3000});
            });
    }

    const onAcceptMultipleDelete = () => {
        setDeleteLoading(true);
        var params = selectedSuppliers.map(p => p.id);  // Send only the array of ids
        axiosInstance.post(baseApi() + "/deleteMulti", params)
            .then((res) => {
                setDeleteLoading(false);
                setSelectedSuppliers(null);
                setVisibleConfirmMultipleDeleteDialog(false);
                querySupplierList(0, tableLimit, false, false);
                toast.current.show({severity:'success', detail: res.data.message, life: 3000});
            })
            .catch((error) => {
                setDeleteLoading(false);
                toast.current.show({severity:'error', detail: "Error deleting supplier", life: 3000});
            });
    }

    const onClickExport = () => {
        setExportLoading(true);
        axiosInstance.post(baseApi() + "/export", queryParams, {
            responseType: 'blob', // Important for handling binary data
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then((response) => {
                setExportLoading(false);
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', "Suppliers_Export.xlsx");
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            })
            .catch((error) => {
                setExportLoading(false);
                toast.current.show({severity:'error', detail: "Error exporting data", life: 3000});
            });
    }

    const onClickDownloadImportTemplate = () => {
        setDownloadTemplateLoading(true);
        axiosInstance.post(baseApi() + "/importTemplate", null, {
            responseType: 'blob',
        })
            .then((response) => {
                setDownloadTemplateLoading(false);
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', 'Suppliers_Import.xlsx'); // Set the file name
                document.body.appendChild(link);
                link.click();
                link.remove();
            })
            .catch((error) => {
                setDownloadTemplateLoading(false);
                toast.current.show({severity:'error', detail: "Error downloading template", life: 3000});
            });
    }

    const clearSupplier = () => {
        setLocalSupplier({
            lastName: "",
            firstName: "",
            middleName: "",
            birthday: "",
            address: "",
            mobileNumber: "",
            telephoneNumber: "",
            email: "",
            facebook: "",
            instagram: "",
            image: "",
            motherName: "",
            fatherName: ""
        });
    };

    const addHeadersToImport = (event) => {
        if (auth && auth !== 'undefined') {
            const token = auth.startsWith('Bearer ') ? auth : `Bearer ${auth}`;
            event.xhr.setRequestHeader("Authorization", token);
            event.xhr.withCredentials = true; // Ensure credentials are included
        } else {
            console.error("Authorization token is missing or invalid");
        }
    };

    const headerTable = () => {
        return <div>
            <div className="grid">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-start gap-2">
                        <Button label="Add" icon="pi pi-plus" onClick={onClickAdd} size="small" />
                        <Button label="Import" icon="pi pi-download" onClick={() => setVisibleImportDialog(true)} size="small" />
                        <Button label="Export" icon="pi pi-upload" onClick={onClickExport} loading={exportLoading} size="small" />
                        {
                            selectedSuppliers && selectedSuppliers.length > 0
                                ? <Button label="Delete" icon="pi pi-trash" severity="danger" loading={deleteLoading}
                                          onClick={() => setVisibleConfirmMultipleDeleteDialog(true)} size="small" /> : null
                        }
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

    const footerAddSupplierDialog = (
        <div>
            <Button label="Cancel" onClick={() => setVisibleAddDialog(false)} size="small" text />
            <Button label="Save" icon="pi pi-save" loading={saveLoading} size="small" autoFocus
                    onClick={onClickSaveSupplier} />
        </div>
    );

    const footerImportDialog = (
        <div>
            <Button label="Download Template" icon="pi pi-download" loading={downloadTemplateLoading}
                    onClick={onClickDownloadImportTemplate} outlined />
        </div>
    );

    const actionColumnLayout = (rowData) => {
        return (
            <div>
                <Button type="button" icon="pi pi-external-link" className="p-button-rounded p-button-text" tooltip="Edit"
                        onClick={() => onClickEdit(rowData)} tooltipOptions={{position:'left'}}></Button>
                <Button type="button" icon="pi pi-trash" className="p-button-rounded p-button-danger p-button-text" tooltip="Delete"
                        onClick={() => onClickSingleDelete(rowData)} tooltipOptions={{position:'left'}}></Button>
            </div>
        );
    }

    const openDialog = (image) => {
        setSelectedImage(`/supplier-image/${image?.image || 'SupplierTempImg.jpg'}`);
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
                    src={`/supplier-image/${image?.image || 'SupplierTempImg.jpg'}`}
                    alt="Image Not Found"
                    width="90"
                    height="60"
                    style={{ borderRadius: '35px', objectFit: 'cover' }}
                />
            </div>
        );
    };

    return (
        <div className="grid">
            <Toast ref={toast} />
            <div className="col-12 md:col-12">
                <Accordion>
                    <AccordionTab header="Filters">
                        <div className="p-fluid">
                            <div className="grid mt-2">
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="lastName" value={queryParams.lastName}
                                                   onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="lastName">Last Name</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="firstName" value={queryParams.firstName}
                                                   onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="firstName">First Name</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="middleName" value={queryParams.middleName}
                                                   onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="middleName">Middle Name</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText id="address" value={queryParams.address}
                                                   onChange={(e) => onChangeQueryParam(e)} />
                                        <label htmlFor="address">Address</label>
                                    </span>
                                </div>
                                <div className="field col-4 md:col-4">
                                    <span className="p-float-label">
                                        <InputText
                                            id="mobileNumber" value={queryParams.mobileNumber}
                                            onChange={(e) => onChangeQueryParam(e)}
                                        />
                                        <label htmlFor="mobileNumber">Mobile Number</label>
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
            <div className="col-12 md:col-12">
                <DataTable ref={dataTable} dataKey="id" value={suppliers} rows={tableLimit}
                           selectionMode={'checkbox'} selection={selectedSuppliers} onSelectionChange={(e) => setSelectedSuppliers(e.value)}
                           emptyMessage="No data" loading={tableLoading} header={headerTable} showGridlines>
                    <Column selectionMode="multiple" headerStyle={{ width: '3rem' }}></Column>
                    <Column field="image" header="Image" body={stockImageTemplate} headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }}/>
                    <Column field="lastName" header="Last Name" headerStyle={{ width: 'auto', minWidth: '20rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="firstName" header="First Name" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="middleName" header="Middle Name" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="birthday" header="Birth Day" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="address" header="Address" headerStyle={{ width: 'auto', minWidth: '20rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="mobileNumber" header="Mobile #" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="telephoneNumber" header="Telephone #" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="email" header="Email" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="facebook" header="Facebook" headerStyle={{ width: 'auto', minWidth: '20rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="instagram" header="Instagram" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="motherName" header="Mother's Name" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column field="fatherName" header="Father's Name" headerStyle={{ width: 'auto', minWidth: '10rem', padding: '10px', textAlign: 'center' }} sortable />
                    <Column header="Action" style={{ minWidth: '1rem'}} body={actionColumnLayout}  headerStyle={{ width: 'auto', minWidth: '2rem', padding: '10px', textAlign: 'center' }} alignFrozen="right"/>
                </DataTable>
                <Paginator ref={paginator} template={paginatorTemplate} first={tableStart} rows={tableLimit} totalRecords={tableTotalCount}
                           onPageChange={(e) => onPageChange(e)} />
            </div>

            <Dialog header={`${action === "CREATE" ? "Add" : "Update"} Supplier`} visible={visibleAddDialog} onHide={() => setVisibleAddDialog(false)}
                    footer={footerAddSupplierDialog} style={{ width: '40vw' }}>
                <div className="p-fluid">
                    <div className="grid mt-4">
                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="lastName" value={localSupplier.lastName} onChange={onChangeSupplier} />
                            <label htmlFor="lastName">Last Name</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="firstName" value={localSupplier.firstName} onChange={onChangeSupplier} />
                            <label htmlFor="firstName">First Name</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="middleName" value={localSupplier.middleName} onChange={onChangeSupplier} />
                            <label htmlFor="middleName">Middle Name</label>
                          </span>
                        </div>

                        <div className="field col-6">
                            <span className="p-float-label">
                                <Calendar
                                    id="birthday"
                                    value={localSupplier.birthday ? new Date(localSupplier.birthday) : null}
                                    onChange={(e) => {
                                        const formattedDate = e.value ? format(e.value, 'yyyy-MM-dd') : '';
                                        onChangeSupplier({ target: { id: 'birthday', value: formattedDate } });
                                    }}
                                    onInput={(e) => {
                                        const input = e.target as HTMLInputElement;
                                        input.value = input.value.replace(/[a-zA-Z]/g, '');
                                    }}
                                    dateFormat="yy-mm-dd"
                                    showIcon
                                />
                                <label htmlFor="birthday">Birthday (YYYY‑MM‑DD)</label>
                            </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="address" value={localSupplier.address} onChange={onChangeSupplier} />
                            <label htmlFor="address">Address</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="mobileNumber" value={localSupplier.mobileNumber} onChange={onChangeSupplier} />
                            <label htmlFor="mobileNumber">Mobile Number</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="telephoneNumber" value={localSupplier.telephoneNumber} onChange={onChangeSupplier} />
                            <label htmlFor="telephoneNumber">Telephone Number</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="email" value={localSupplier.email} onChange={onChangeSupplier} />
                            <label htmlFor="email">Email</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="facebook" value={localSupplier.facebook} onChange={onChangeSupplier} />
                            <label htmlFor="facebook">Facebook</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="instagram" value={localSupplier.instagram} onChange={onChangeSupplier} />
                            <label htmlFor="instagram">Instagram</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="motherName" value={localSupplier.motherName} onChange={onChangeSupplier} />
                            <label htmlFor="motherName">Mother's Name</label>
                          </span>
                        </div>

                        <div className="field col-6">
                          <span className="p-float-label">
                            <InputText id="fatherName" value={localSupplier.fatherName} onChange={onChangeSupplier} />
                            <label htmlFor="fatherName">Father's Name</label>
                          </span>
                        </div>

                        <div className="field col-12 md:col-12">
                            <FileUpload
                                mode="basic"
                                name="image"
                                accept="image/*"
                                maxFileSize={5000000}
                                customUpload
                                uploadHandler={null}
                                onSelect={onFileSelect}
                                chooseOptions={{ icon: "pi pi-image", label: "Choose file" }}
                            />
                        </div>

                        <div>
                            {localSupplier.image ? (
                                <div className="flex flex-wrap justify-content-center">
                                    <img
                                        src={`/supplier-image/${localSupplier.image}`}
                                        className="mt-3"
                                        width="250"
                                        alt="Supplier Image"
                                    />
                                </div>
                            ) : null}
                        </div>
                    </div>
                </div>
            </Dialog>

            <Dialog
                header="Import Suppliers"
                visible={visibleImportDialog}
                onHide={() => setVisibleImportDialog(false)}
                footer={footerImportDialog}
                style={{ width: '50vw' }}
            >
                <div>
                    <FileUpload
                        name="file"
                        accept=".xlsx,.xls"
                        maxFileSize={1000000}
                        customUpload
                        uploadHandler={onUploadSuppliers}
                        onBeforeSend={addHeadersToImport}
                        chooseLabel="Choose"
                        uploadLabel="Upload"
                    />
                </div>
            </Dialog>

            <Dialog
                header="Supplier Image"
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

            <ConfirmDialog visible={visibleConfirmSingleDeleteDialog} onHide={() => setVisibleConfirmSingleDeleteDialog(false)}
                           message="Are you sure you want to delete?" header="Confirmation" icon="pi pi-exclamation-triangle"
                           accept={onAcceptSingleDelete} reject={() => setVisibleConfirmSingleDeleteDialog(false)} />

            <ConfirmDialog visible={visibleConfirmMultipleDeleteDialog} onHide={() => setVisibleConfirmMultipleDeleteDialog(false)}
                           message={`Are you sure you want to delete these ${selectedSuppliers?.length} supplier${selectedSuppliers?.length > 1 ? "s" : ""}?`}
                           header="Confirmation" icon="pi pi-exclamation-triangle"
                           accept={onAcceptMultipleDelete} reject={() => setVisibleConfirmMultipleDeleteDialog(false)} />
        </div>
        // 123123123
    );
};
export default Supplier;
