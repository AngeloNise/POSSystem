
'use client';
import React, { useState, useRef, useEffect } from 'react';
import { Accordion, AccordionTab } from 'primereact/accordion';
import { Button } from 'primereact/button';
import { Chips } from 'primereact/chips';
import { Column } from 'primereact/column';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { Fieldset } from 'primereact/fieldset';
import { FilterMatchMode, FilterOperator } from 'primereact/api';
import { InputText } from 'primereact/inputtext';
import { Image } from 'primereact/image';
import { Paginator } from 'primereact/paginator';
import { Ripple } from 'primereact/ripple';
import { ScrollPanel } from 'primereact/scrollpanel';
import { SelectButton } from 'primereact/selectbutton';
import { SplitButton } from 'primereact/splitbutton';
import { Tag } from 'primereact/tag';
import { Toast } from 'primereact/toast';
import { Tree } from 'primereact/tree';


import styles from './index.module.scss';
import { classNames } from 'primereact/utils';

import useAxiosInstance from '@/util/CustomAxios'
import useAuthStore from '@/store/AuthStore';
import { useRouter } from 'next/navigation';
import { FileUpload } from 'primereact/fileupload';

import AccountService from '@/service/access_management/AccountService'
import { error } from 'console';

const AccountManagement = () => {
    const dataTable = useRef(null);
    const paginator = useRef(null);
    const toast = useRef(null);
    const fileUpload = useRef(null);

    const axiosInstance = useAxiosInstance();
    const router = useRouter();
    const { userInfo } = useAuthStore();
    const accountService = new AccountService(userInfo);

    const [accountList, setAccountList] = useState(null);
    const [rolesList, setRolesList] = useState(null);
    const [orgTree, setOrgTree] = useState(null);
    const [organizationList, setOrganizationList] = useState(null);

    const [account, setAccount] = useState(null);
    const [signature, setSignature] = useState(null);
    const [selectedNat, setSelectedNat] = useState({});
    const [selectedGen, setSelectedGen] = useState({});
    const [selectedStat, setSelectedStat] = useState({});
    const [selectedType, setSelectedType] = useState('');
    const [selRole, setSelRole] = useState(null);
    const [disabledForm, setDisabledForm] = useState(true);
    const [actionType, setActionType] = useState("none");
    const [selectedSourceMenu, selectedSourceMenuSet] = useState('');
    const [dialogRole, setDialogRole] = useState(false);
    const [dialogOrg, setDialogOrg] = useState(false);
    const [roleOrgCont, roleOrgContSet] = useState([]);
    const [roleOrgSel, roleOrgSelSet] = useState([]);
    const [removeItemList, removeItemListSet] = useState([]);
    const [isBtnAddRelOrg, isBtnAddRelOrgSet] = useState('hidden');
    const [buttonPermission, buttonPermissionSet] = useState({});
    const [uploadUserExcel, uploadUserExcelSet] = useState(false);

    const [dialogSourceMenu, dialogSourceMenuSet] = useState(false);
    const [displayPasswordChange, displayPasswordChangeSet] = useState(false);
    const [newPassword, newPasswordSet] = useState({
        "password": "",
        "confirmPassword": ""
    });
    const [globalFilterValue, setGlobalFilterValue] = useState('');

    const [isRoleUpdate, setIsRoleUpdate] = useState(false);
    const [isOrgUpdate, setIsOrgUpdate] = useState(false);
    const [isFirstLoad, isFirstLoadSet] = useState(true);

    const [getSelectedFile, setSelectedFile] = useState(false);
    const [uploadResults, setUploadResults] = useState(null);

    const [tableStart, setTableStart] = useState(0);
    const [tableLimit, setTableLimit] = useState(10);
    const [tableTotalCount, setTableTotalCount] = useState(0);
    const [tableLoading, setTableLoading] = useState(false);

    const [searchLoading, setSearchLoading] = useState(false);
    const [refreshLoading, setRefreshLoading] = useState(false);
    const [saveLoading, setSaveLoading] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const [visibleAddDialog, setVisibleAddDialog] = useState(false);
    const [visibleConfirmSingleDeleteDialog, setVisibleConfirmSingleDeleteDialog] = useState(false);
    const [visibleConfirmMultipleDeleteDialog, setVisibleConfirmMultipleDeleteDialog] = useState(false);


    const [filters, setFilters] = useState({
        global: { value: null, matchMode: FilterMatchMode.CONTAINS }
    });


    const [displayBasic, setDisplayBasic] = useState(false);
    const [removeRoleOrgPanel, removeRoleOrgPanelSet] = useState(true);


    const nationality = [
        { name: 'FILIPINO', id: '2' },
        { name: 'CHINESE', id: '1' }
    ];

    const gender = [
        { name: 'MALE', id: '1' },
        { name: 'FEMALE', id: '0' }
    ];

    const type = [
        'USER',
        'KEEPER'
    ];

    const status = [
        { name: 'PROBATION', id: '0' },
        { name: 'REGULAR', id: '1' },
        { name: 'RESIGN', id: '2' }
    ];

    const sourceMenuOptions = ['ROLE', 'USER'];

    const chooseOptions = {
        icon: 'pi pi-fw pi-images', iconOnly: false,
        className: 'custom-choose-btn p-button-rounded p-button-outlined custom-w-max', label: 'Upload Signature'
    };

    const cssCenter = {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center'
    }

    const btnStyle = {
        hideButton: {
            display: 'none',
            alignItems: 'center'
        },
        showButton: {
            display: 'inline-flex',
            alignItems: 'center'
        }
    }

    const uploadOptions = { label: 'Upload File', style: getSelectedFile === true ? btnStyle.showButton : btnStyle.hideButton };

    useEffect(() => {
        queryAllAccounts();
        getWidgetPermission();
    }, []);

    useEffect(() => {
        if (roleOrgCont.length > 0) {

            if (actionType === 'edit') {
                if (isFirstLoad) {
                    isFirstLoadSet(false);
                    // Wrap the string in an array if roleOrgSelSet expects an array
                    roleOrgSelSet([`${account.role_id}:${account.orgid}`]);
                    setAccount({
                        ...account,
                        roleOrgMain: `${account.role_id}:${account.orgid}`,
                        roleOrgList: roleOrgCont,
                        UPDATED_BY: userInfo.ACCOUNT_ID
                    });
                } else {
                    // Pass an array here as well
                    roleOrgSelSet([roleOrgCont[roleOrgCont.length - 1].value]);
                    setAccount({
                        ...account,
                        roleOrgMain: roleOrgCont[roleOrgCont.length - 1].value,
                        roleOrgList: roleOrgCont,
                        UPDATED_BY: userInfo.ACCOUNT_ID
                    });
                }
            } else {
                // Pass an array here as well
                roleOrgSelSet([roleOrgCont[roleOrgCont.length - 1].value]);
                setAccount({
                    ...account,
                    roleOrgMain: roleOrgCont[roleOrgCont.length - 1].value,
                    roleOrgList: roleOrgCont,
                    UPDATED_BY: userInfo.ACCOUNT_ID
                });
            }
        }
    }, [roleOrgCont]);

    /** API */

    const queryAllAccounts = () => {
        console.log(`queryAllAccounts: ${JSON.stringify(userInfo)}`);
        setTableLoading(true);
        accountService.showAllAccountsByOrg()
            .then(res => {
                setTableLoading(false);
                setAccountList(res.data["queryAccounts"]);
                setRolesList(res.data["queryRoles"]);
                setOrgTree(res.data["queryOrgTree"]);
                setOrganizationList(res.data["queryOrganization"]);
            })
            .catch(err => {
                setTableLoading(false);
            });
    }

    const accountActivation = (id) => {
        const single = accountList.filter(x => id == x.ACCOUNT_ID);

        const params = { name: 'accountActivation', conditions: null, action: null, object: single[0], userId: userInfo?.ACCOUNT_ID, userFullName: userInfo?.FULL_NAME };

        accountService.accountActivation(params)
            .then((response) => {
                toast.current.show({ severity: 'success', summary: 'Account Page', detail: 'Update Success', life: 3000 });
                queryAllAccounts();
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Account Page', detail: errMsg, life: 3000 });
            });
    }

    //TODO
    const accountsUpdate = () => {

        if (roleOrgCont.length == 0) {
            toast.current.show({ severity: 'error', summary: 'Account Page', detail: 'Role and Organization not selected', life: 3000 });
            return null;
        }

        const condition = [];
        if (isRoleUpdate) condition.push("roleUpdate");
        if (isOrgUpdate) condition.push("orgUpdate");

        var files = fileUpload.current.files;

        const formData = new FormData();
        formData.append("name", "accountUpdate");
        formData.append("action", actionType);
        formData.append("object", JSON.stringify(account));
        formData.append("signature", files ? files[0] : null);
        formData.append("userId", userInfo?.ACCOUNT_ID);
        formData.append("userFullName", userInfo?.FULL_NAME);
        accountService.accountsUpdate(formData)
            .then((response) => {
                if (response.data.feedback === '1') {
                    toast.current.show({ severity: 'success', summary: 'Account Page', detail: 'Update Success', life: 3000 });
                    setDisplayBasic(false);
                    setIsRoleUpdate(false);
                    setIsOrgUpdate(false);
                    isFirstLoadSet(true);
                    queryAllAccounts();
                } else {
                    toast.current.show({ severity: 'error', summary: 'Account Page', detail: 'Missing required fields', life: 5000 });
                }
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Account Page', detail: errMsg, life: 5000 });
            });
    }

    const getWidgetPermission = () => {
        let params = {
            userid: userInfo?.ACCOUNT_ID,
            companyId: userInfo?.COMPANY_ID,
            resourceid: "45",
            roleid: userInfo?.ROLE_ID,
            sourcemenu: userInfo?.SOURCE_MENU
        };
        accountService.getWidgetPermission(params)
            .then((res) => {
                buttonPermissionSet(res.data);
            }).catch((err) => {
                //TODO
            });
    }


    const switchSourceMenuSave = () => {
        const params = {
            name: 'SwitchSourceMenuSave',
            str: selectedSourceMenu,
            object: account,
            userId: userInfo?.ACCOUNT_ID,
            userFullName: userInfo?.FULL_NAME
        };
        accountService.switchSourceMenuSave(params)
            .then((response) => {
                toast.current.show({ severity: 'success', summary: 'Account Page', detail: 'Update Success', life: 3000 });
                queryAllAccounts();
                dialogSourceMenuSet(false);

                //TODO
                /* let newData_ = {SOURCE_MENU: selectedSourceMenu};
                let newUserTemplate_ = UserTemplate(userInfo, newData_);
                setUserDetailsProx(newUserTemplate_); */
            },
                (error) => {
                    const _content =
                        (error.response && error.response.data) || error.message || error.toString();
                    toast.current.show({ severity: 'error', summary: 'Rental Page', detail: _content, life: 3000 });
                }
            );
    }

    const getSelectedCustomer = (id) => {
        console.log(`getSelectedCustomer | id: ${id}`);
        const single = accountList.filter(x => id == x.ACCOUNT_ID);
        console.log(single[0]);
        setAccount(single[0]);

        var natId = single[0].NATIONALITY;

        if (natId !== '') {
            var sel = nationality.filter(y => y.id == natId);
            setSelectedNat(sel[0]);

        } else {
            setSelectedNat(nationality[0]);
        }

        var sexId = single[0].SEX;
        var selGen = gender.filter(z => z.id == sexId);
        setSelectedGen(selGen[0]);

        var statusId = single[0].STATUS_;
        var selStatus = status.filter(aa => aa.id == statusId);
        setSelectedStat(selStatus[0]);

        var typeName = single[0].TYPE;
        var selTyp = type.filter(t => t == typeName);
        setSelectedType(selTyp[0]);
    }

    /** LISTENERS */

    const onGlobalFilterChange = (e) => {
        const value = e.target.value;
        let _filters = { ...filters };

        _filters['global'].value = value;

        setFilters(_filters);
        setGlobalFilterValue(value);
    };

    const onShowAccountDetailsDialog = () => {
        if (account?.SIGNATURE && (actionType === "view" || actionType === "edit")) {
            accountService.viewSignature(account?.SIGNATURE)
                .then((res) => {
                    const blob = new Blob([res.data], { type: 'image/png' });
                    let objUrl = { image: URL.createObjectURL(blob) };
                    setSignature(objUrl);
                })
                .catch((error) => {
                    console.log(error);
                    toast.current.show({ severity: 'error', summary: 'Error loading signature', detail: 'Unable to retrieve signature', life: 3000 });
                });
        } else {
            fileUpload?.current?.clear();
            setSignature(null);
        }
    }

    const onSelectSignature = () => {
        setSignature({ image: fileUpload.current.files[0].objectURL });
    }

    const onDeleteSignature = () => {
        fileUpload.current.clear();
        setSignature(null);
        setAccount({ ...account, SIGNATURE: null });
    }

    const onUploadHandler = async (event) => {
        for (const element of event.files) {
            let formData = new FormData();
            formData.append("excel[]", element)
            formData.append("username", 'username');
            formData.append("userId", userInfo?.ACCOUNT_ID);
            formData.append("userFullName", userInfo?.FULL_NAME);

            accountService.uploadNewUserFromExcel(formData)
                .then((res) => {
                    setUploadResults(res.data.data);
                    setSelectedFile(false);
                })
                .catch((err) => {
                    //TODO
                });
        }
    }

    /** LAYOUTS */

    const headerTable = () => {
        return <div>
            <div className="grid">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-start gap-2">
                        <Button label="Add" icon="pi pi-plus" className={`${buttonPermission["w014x45"]?.display || 'hidden'}`}
                            size="small" onClick={() => {
                                setDisplayBasic(true);
                                setDisabledForm(false);
                                setAccount({
                                    CREATED_BY: userInfo?.ACCOUNT_ID,
                                    SEX: '1',
                                    NATIONALITY: '2',
                                    STATUS_: '1',
                                    CREATED_BY_ID: userInfo?.ACCOUNT_ID,
                                    CREATED_BY_NAME: userInfo?.FULL_NAME,
                                });
                                setSelectedNat(nationality[0]);
                                setSelectedGen(gender[0]);
                                setSelectedStat(status[0]);

                                setActionType("add");
                            }} />
                        <Button label="Import" icon="pi pi-download" className={`${buttonPermission["w015x45"]?.display || 'hidden'}`}
                            size="small" text onClick={() => uploadUserExcelSet(true)} />
                    </div>
                </div>
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                        <span className="p-input-icon-left">
                            <i className="pi pi-search" />
                            <InputText value={globalFilterValue} onChange={onGlobalFilterChange}
                                className="p-inputtext-sm" placeholder="Account Search" />
                        </span>
                    </div>
                </div>
            </div>
        </div>
    }

    const actionBodyTemplate = (row) => {
        return (
            <div>
                <span>
                    <Button data-id={row.ACCOUNT_ID} icon="pi pi-book" className="p-button-outlined p-button-success mr-2 mb-2"
                        tooltip="View" size="small"
                        onClick={(e) => {
                            const element = (e.target as HTMLElement);
                            console.log(element);
                            setDisplayBasic(true);
                            const id = row.ACCOUNT_ID;//element.id;
                            getSelectedCustomer(id);
                            setDisabledForm(true);
                            setActionType("view");
                        }} />
                    <Button data-id={row.ACCOUNT_ID} icon="pi pi-user-edit" className="p-button-outlined p-button-warning mr-2 mb-2"
                        tooltip="Edit" size="small"
                        onClick={(e) => {
                            const element = (e.target as HTMLElement);
                            setDisplayBasic(true);
                            const id = row.ACCOUNT_ID;//element.id;
                            getSelectedCustomer(id);
                            setDisabledForm(false);
                            setActionType("edit");
                        }} />

                    <Button icon="pi pi-user-minus" data-id={row.ACCOUNT_ID}
                        className={`p-button-outlined p-button-${row.STATE == '1' ? 'danger' : 'info'} mr-2 mb-2`}
                        tooltip={row.STATE === '1' ? 'disable' : 'enable'} size="small"
                        onClick={(e) => {
                            const element = (e.target as HTMLElement);
                            const id = row.ACCOUNT_ID;//element.id;
                            accountActivation(id);
                        }} />

                    <Button icon="pi pi-arrow-right-arrow-left" data-id={row.ACCOUNT_ID}
                        className="p-button-outlined p-button-warning mr-2 mb-2"
                        tooltip="Switch Resource Menu" size="small"
                        onClick={(e) => {
                            const element = (e.target as HTMLElement);
                            dialogSourceMenuSet(true);

                            const id = row.ACCOUNT_ID;//element.id;
                            const single = accountList.filter(x => id == x.ACCOUNT_ID);
                            setAccount(single[0]);

                            selectedSourceMenuSet(single[0].SOURCE_MENU);
                        }} />

                    <Button icon="pi pi-lock"
                        className="p-button-outlined p-button-help mr-2 mb-2"
                        tooltip="Change Password" size="small"
                        onClick={(e) => {
                            displayPasswordChangeSet(true);
                            const single = accountList.filter(x => row.ACCOUNT_ID == x.ACCOUNT_ID);
                            setAccount(single[0]);
                        }} />
                </span>
            </div>
        );
    }

    const genderBodyTemplate = (param) => {
        return <span className={`customer-badge status-${param.SEX == '1' ? 'new' : 'unqualified'}`}>
            {param.SEX == '1' ? 'MALE' : 'FEMALE'}
        </span>;
    }

    const stateBodyTemplate = (param) => {
        return <span className={`customer-badge status-${param.STATE == '1' ? 'qualified' : 'unqualified'}`}>
            {param.STATE == '1' ? 'ACTIVE' : 'INACTIVE'}
        </span>;
    }

    const footerDialogAccountDetails = (
        <div>
            <Button type="button" label="Dismiss" size="small" text
                onClick={() => setDisplayBasic(false)}
            />
            <Button type="button" label="Save" className="p-button-primary" size="small"
                disabled={disabledForm} onClick={() => accountsUpdate()} />
        </div>
    );

    const customChip = (item) => {
        let strNew = item.split('===');
        return (
            <div>
                <span>
                    {strNew[0]}
                </span>
            </div>
        );
    };

    const formatRemarksColumn = (rowData) => {
        return (!rowData.remarks ? <div></div>
            : rowData.remarks.split('\n').map((str, index) => <p key={index}>{str}</p>));
    }

    const formatStatusColumn = (rowData) => {
        return (!rowData.status ? <div></div>
            : <div><Tag className="mr-2" severity={rowData.status === "Uploaded" || rowData.status === "Success" ? "success" : "danger"} value={rowData.status}></Tag></div>);
    }

    const footerSourceMenu = (
        <>
            <Button type="button" label="Dismiss" size="small" text
                onClick={() => dialogSourceMenuSet(false)}
            />
            <Button type="button" label="Save" className="p-button-primary" size="small"
                onClick={() => {
                    switchSourceMenuSave();
                }}
            />
        </>
    );

    const changePasswordFooter = (
        <>
            <Button type="button" label="Dismiss" size="small" text
                onClick={() => displayPasswordChangeSet(false)}
            />
            <Button type="button" label="Save" className="p-button-primary" size="small"
                onClick={() => {
                    if (!newPassword.password) {
                        toast.current.show({ severity: 'error', summary: 'System Information', detail: "No input", life: 4000 });
                        return;
                    }

                    if (!newPassword.confirmPassword) {
                        toast.current.show({ severity: 'error', summary: 'System Information', detail: "Please confirm new password", life: 4000 });
                        return;
                    }

                    if (newPassword.password !== newPassword.confirmPassword) {
                        toast.current.show({ severity: 'error', summary: 'System Information', detail: "Password Mismatch", life: 4000 });
                        return false;
                    }

                    let objectV = {
                        account_id: account.ACCOUNT_ID,
                        password: newPassword.password,
                        userId: userInfo.ACCOUNT_ID,
                        userFullName: userInfo.FULL_NAME,
                        pageName: "User Management" // Add parameter indicating the page name
                    };

                    accountService.editUserPassword(objectV).then(
                        (res) => {
                            if (res.data.status === 200) {
                                toast.current.show({ severity: 'success', summary: 'System Information', detail: "Change password successful.", life: 4000 });
                                displayPasswordChangeSet(false);
                                newPasswordSet({
                                    "password": "",
                                    "confirmPassword": ""
                                });
                            } else {
                                toast.current.show({ severity: 'error', summary: 'System Information', detail: res.data.errMsg });
                            }
                        },
                        (error) => {
                            // Handle error
                        }
                    );
                }}
            />
        </>
    );

    return (
        <div className="grid">
            <Toast ref={toast} />
            <div className="col-12 md:col-12">
                <DataTable value={accountList} paginator className="p-datatable-gridlines" showGridlines rows={10}
                    dataKey="id" header={headerTable} filters={filters} loading={tableLoading} responsiveLayout="scroll"
                    emptyMessage="No users found" globalFilterFields={['FULL_NAME', 'ACCOUNT_NAME', 'role']}>
                    <Column header="ACTION" body={actionBodyTemplate} style={{ minWidth: '20rem' }} />

                    <Column field="SOURCE_MENU" header="Source Menu" style={{ minWidth: '10rem' }} />

                    <Column field="ACCOUNT_ID" header="Account ID" style={{ minWidth: '8rem' }} />
                    <Column field="ACCOUNT_NAME" header="Account" style={{ minWidth: '12rem' }} />
                    <Column field="FULL_NAME" header="Name" style={{ minWidth: '12rem' }} />
                    <Column field="role" header="Role" style={{ minWidth: '12rem' }} />
                    <Column field="MOBILE_PHONE_A" header="Contact #" style={{ minWidth: '12rem' }} />

                    <Column field="SEX" header="Gender" style={{ minWidth: '12rem' }} body={genderBodyTemplate} />
                    <Column field="STATE" header="State" style={{ minWidth: '12rem' }} body={stateBodyTemplate} />
                    <Column field="CREATE_DATE_F" header="Create Date" style={{ minWidth: '12rem' }} />
                </DataTable>
            </div>

            {/* DIALOGS */}
            <Dialog header="Account Details" visible={displayBasic} className="account-deets" modal footer={footerDialogAccountDetails} onHide={() => setDisplayBasic(false)}
                onShow={() => {
                    onShowAccountDetailsDialog();
                    roleOrgContSet([]);
                    removeItemListSet([]);

                    if (actionType == 'edit') {
                        let userId = account?.ACCOUNT_ID;
                        accountService.getRoleOrgListByUserid(userId)
                            .then((response) => {
                                let roleOrgList_ = response.data;
                                roleOrgList_.map(x => {
                                    removeItemListSet(xo => [...xo, x.name + '===' + x.value]);
                                });
                                roleOrgContSet(response.data);
                            })
                            .catch((error) => {
                                //TODO
                            });
                    }
                }} >
                <br />
                <div style={{ backgroundColor: "#fafafa", padding: '25px', borderRadius: '10px' }}>
                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.FULL_NAME || ''} disabled={disabledForm} style={{ width: '100%' }}
                                    onChange={e => setAccount({ ...account, FULL_NAME: e.target.value })} />
                                <label htmlFor="inputtext">Fullname <span style={{ color: 'red' }}>*</span></label>
                            </span>
                        </div>
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.CREATE_DATE_F || ''} disabled style={{ width: '100%' }}
                                />
                                <label htmlFor="inputtext">Create Date:</label>
                            </span>
                        </div>
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.ACCOUNT_NAME || ''} disabled={actionType == "add" ? false : true} style={{ width: '100%' }}
                                    onChange={e => setAccount({ ...account, ACCOUNT_NAME: e.target.value })} />
                                <label htmlFor="inputtext">Account Name <span style={{ color: 'red' }}>*</span></label>
                            </span>
                        </div>
                    </div>

                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.MOBILE_PHONE_A || ''} disabled={disabledForm} style={{ width: '100%' }}
                                    onChange={e => setAccount({ ...account, MOBILE_PHONE_A: e.target.value })} />
                                <label htmlFor="inputtext">Phone:</label>
                            </span>
                        </div>
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <Dropdown id="dropdown" options={nationality} value={selectedNat} style={{ width: '100%' }} disabled={disabledForm}
                                    onChange={e => {
                                        setSelectedNat(e.value);
                                        var val = e.value.id;
                                        setAccount({ ...account, NATIONALITY: val });
                                    }}
                                    optionLabel="name" />
                                <label htmlFor="dropdown">Nationality</label>
                            </span>
                        </div>
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <Dropdown id="dropdown" options={gender} value={selectedGen} style={{ width: '100%' }} disabled={disabledForm}
                                    onChange={e => {
                                        setSelectedGen(e.value);
                                        var val = e.value.id;
                                        setAccount({ ...account, SEX: val });
                                    }}
                                    optionLabel="name" />
                                <label htmlFor="dropdown">GENDER</label>
                            </span>
                        </div>
                    </div>

                    <div className="grid">
                        <div className="field col-12 md:col-12">
                            <span className="p-float-label">
                                <InputText
                                    style={{ width: '100%' }}
                                    type="text" id="inputtext" value={account?.HOME_ADDRESS || ''} disabled={disabledForm}
                                    onChange={e => setAccount({ ...account, HOME_ADDRESS: e.target.value })} />
                                <label htmlFor="inputtext">Address :</label>
                            </span>
                        </div>

                    </div>

                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.EMPLOYEE_ID || ''} disabled={disabledForm} style={{ width: '100%' }}
                                    onChange={e => setAccount({ ...account, EMPLOYEE_ID: e.target.value })} />
                                <label htmlFor="inputtext">EMPLOYEE ID :</label>
                            </span>
                        </div>
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.EMAIL1 || ''} disabled={disabledForm} style={{ width: '100%' }}
                                    onChange={e => setAccount({ ...account, EMAIL1: e.target.value })} />
                                <label htmlFor="inputtext">Email :</label>
                            </span>
                        </div>
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <Dropdown id="dropdown" options={status} value={selectedStat} style={{ width: '100%' }} disabled={disabledForm}
                                    onChange={e => {
                                        setSelectedStat(e.value);
                                        var val = e.value.id;
                                        setAccount({ ...account, STATUS_: val });
                                    }}
                                    optionLabel="name" />
                                <label htmlFor="dropdown">STATUS</label>
                            </span>
                        </div>
                    </div>

                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <Dropdown id="dropdown" options={type} value={selectedType} style={{ width: '100%' }} disabled={disabledForm}
                                    onChange={e => {
                                        setSelectedType(e.value);
                                        setAccount({ ...account, TYPE: e.value });
                                    }}
                                />
                                <label htmlFor="dropdown">TYPE</label>
                            </span>

                        </div>

                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <InputText type="text" id="inputtext" value={account?.POSITION || ''} style={{ width: '100%' }} disabled={disabledForm}
                                    onChange={e => {
                                        setAccount({ ...account, POSITION: e.target.value });
                                    }}
                                />
                                <label htmlFor="inputtext">Position :</label>
                            </span>
                        </div>
                    </div>

                    <div className="grid">
                        <div className="field col-12 md:col-4">
                            <div className="p-inputgroup">
                                <span className="p-float-label">
                                    <InputText type="text" id="inputtext" value={account?.role || ''} disabled style={{ width: '100%' }} />
                                    <label htmlFor="dropdown">Role <span style={{ color: 'red' }}>*</span></label>
                                </span>
                                <Button icon="pi pi-search" className="p-button-info" disabled={disabledForm}
                                    onClick={() => {
                                        setDialogRole(true);
                                        const selRol = rolesList.filter(x => x.ROLE_ID == account?.role_id);
                                        setSelRole(selRol[0]);
                                    }} />
                            </div>
                        </div>

                        <div className="field col-12 md:col-4">
                            <div className="p-inputgroup">
                                <span className="p-float-label">
                                    <InputText type="text" id="inputtext" value={account?.organization || ''} disabled style={{ width: '100%' }} />
                                    <label htmlFor="dropdown">Organization <span style={{ color: 'red' }}>*</span></label>
                                </span>
                                <Button icon="pi pi-search" className="p-button-info"
                                    onClick={() => setDialogOrg(true)} disabled={disabledForm} />
                            </div>
                        </div>

                        <div className="field col-12 md:col-4">
                            <Button label="Add Role - Organization Relation" className={`btn btn-primary ${isBtnAddRelOrg}`}
                                onClick={e => {
                                    roleOrgContSet([...roleOrgCont, { name: account?.role + '-' + account?.organization, value: account?.role_id + ':' + account?.orgid }]);
                                    removeItemListSet(xo => [...xo, account?.role + '-' + account?.organization + '===' + account?.role_id + ':' + account?.orgid]);
                                }}
                            />
                        </div>
                        <div className="grid col-12 md:col-12">
                            <div className="col-12">
                                <SelectButton value={roleOrgSel} onChange={e => {
                                    roleOrgSelSet(e.value);

                                    console.log(organizationList);

                                    let orgId = e.value.split(":")[1];

                                    let orgFil = organizationList.filter(xx => xx.ORGANIZATION_ID.toString() === orgId);

                                    let orgtypeStr = orgFil[0].type_;

                                    let orgpathStr = orgFil[0].path_;

                                    setAccount({ ...account, roleOrgMain: e.value, orgtype: orgtypeStr, orgpath: orgpathStr });

                                    //console.log(account);
                                }} optionLabel="name" options={roleOrgCont} />
                            </div>
                        </div>

                        <div className="grid col-12 md:col-12">
                            <Fieldset legend="remove role- org" toggleable collapsed={removeRoleOrgPanel}>
                                <p className="m-0">
                                    <Chips value={removeItemList} style={{ width: '100%' }} itemTemplate={customChip}
                                        onChange={e => {
                                            if (removeItemList.length > 1) {
                                                removeItemListSet(e.value)
                                            }
                                        }}
                                        onRemove={e => {
                                            //alert(JSON.stringify(e.value));
                                        }} />

                                    <Button label="Apply Changes" icon="pi pi-delete" size="small" className="mt-3"
                                        onClick={e => {
                                            removeRoleOrgPanelSet(true);
                                            let newArrList_ = [];
                                            removeItemList.map(xo => {
                                                let filterRolOrg_ = roleOrgCont.filter(xx => xx.name + '===' + xx.value == xo);
                                                newArrList_.push(filterRolOrg_[0]);
                                            });
                                            roleOrgContSet(newArrList_);
                                        }}
                                    />
                                    &nbsp;&nbsp;&nbsp;
                                    <Button label="Reset" icon="pi pi-refresh" size="small" className="mt-3 p-button-secondary"
                                        onClick={e => {
                                            removeItemListSet([]);
                                            roleOrgCont.map(xo => {
                                                removeItemListSet(xx => [...xx, xo.name + '===' + xo.value]);
                                            });
                                        }}
                                    />

                                </p>

                            </Fieldset>
                        </div>

                        <div className="field col-12 md:col-4">
                            <span className="p-float-label">
                                <label htmlFor="dropdown">Signature</label>
                            </span>
                            <div className="mt-4">
                                {
                                    actionType === "add" || actionType === "edit"
                                        ? <FileUpload mode="basic" name="signature" accept="image/*" maxFileSize={1000000}
                                            customUpload uploadHandler={null} onSelect={onSelectSignature}
                                            ref={fileUpload} chooseOptions={chooseOptions} /> : null
                                }
                                {
                                    signature
                                        ? <div>
                                            <div className="flex flex-wrap justify-content-center">
                                                <Image src={signature.image} className="mt-3" width="250" height="150" />
                                            </div>
                                            <div className="flex flex-wrap justify-content-center">
                                                {
                                                    actionType === "add" || actionType === "edit"
                                                        ? <Button label="Remove" className="p-button p-component p-button-text p-button-danger"
                                                            onClick={onDeleteSignature} /> : null
                                                }
                                            </div>
                                        </div> : null
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </Dialog>

            <Dialog header="Role Selection" visible={dialogRole} className="role-selection" modal onHide={() => setDialogRole(false)}>
                <DataTable value={rolesList} selectionMode="single" selection={selRole} dataKey="ROLE_ID"
                    onSelectionChange={e => {
                        setSelRole(e.value);
                        setDialogRole(false);
                        setAccount({ ...account, role: e.value.NAME_, role_id: e.value.ROLE_ID });
                        setIsRoleUpdate(true);
                    }}>
                    <Column selectionMode="single" headerStyle={{ width: '3em' }} />
                    <Column field="NAME_" header="Role" />
                    <Column field="CODE" header="Code" />
                </DataTable>
            </Dialog>

            <Dialog header="Organization Selection" visible={dialogOrg} className="role-selection" modal onHide={() => setDialogOrg(false)}>
                <ScrollPanel style={{ width: '100%', height: '550px' }}>
                    <Tree value={orgTree} onSelect={e => {
                        setDialogOrg(false);
                        const re = organizationList.filter(x => x.ORGANIZATION_ID == e.node.key);
                        setAccount({ ...account, organization: re[0].FULL_NAME, orgid: re[0].ORGANIZATION_ID, orgtype: re[0].type_, orgpath: re[0].path_ });
                        setIsOrgUpdate(true);
                        isBtnAddRelOrgSet('flex');
                    }
                    } selectionMode="single" />
                </ScrollPanel>
            </Dialog>

            <Dialog header="Source Menu Change" visible={dialogSourceMenu} style={{ width: '20vw' }} modal
                footer={footerSourceMenu} onHide={() => dialogSourceMenuSet(false)} >
                <br />
                <div style={{ background: '#eaeaea', padding: '15px' }}>
                    <div style={cssCenter}>
                        <p><b>{account?.ACCOUNT_NAME}</b></p>
                        <br></br>
                    </div>
                    <div style={cssCenter}>
                        <SelectButton value={selectedSourceMenu} options={sourceMenuOptions} onChange={(e) => selectedSourceMenuSet(e.value)}
                        />
                    </div>
                </div>
            </Dialog>

            <Dialog header="Upload User Data from Excel" draggable={false} resizable={false} className="account-list" visible={uploadUserExcel} modal={true}
                onHide={() => {
                    uploadUserExcelSet(false);
                    setUploadResults(null);
                    queryAllAccounts();
                }} >
                <FileUpload mode="advanced"
                    maxFileSize={30000000}
                    chooseOptions={{ label: 'Select File', icon: 'pi pi-file-excel', className: 'p-button-primary p-button-small' }}
                    cancelOptions={{ label: 'Cancel', icon: 'pi pi-times', className: 'p-button-outlined p-button-small' }}
                    accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                    uploadOptions={uploadOptions}
                    onSelect={() => setSelectedFile(true)}
                    onRemove={() => setSelectedFile(false)}
                    onClear={() => setSelectedFile(false)}
                    customUpload uploadHandler={onUploadHandler}
                />
                <DataTable value={uploadResults} >
                    <Column field="file" header="File" />
                    <Column header="Status" body={formatStatusColumn} />
                    <Column header="Remarks" body={formatRemarksColumn} />
                    <Column field="insertCount" header="Insert Count" />
                    <Column field="duplicateCount" header="Duplicate Count" />

                </DataTable>
            </Dialog>

            <Dialog header="User password change" visible={displayPasswordChange} style={{ width: '20vw' }} modal
                footer={changePasswordFooter} onHide={() => displayPasswordChangeSet(false)} >
                <br />
                <div style={{ background: '#eaeaea', padding: '15px' }}>

                    <div style={cssCenter}>
                        <p><b>{account?.ACCOUNT_NAME}</b></p>
                        <br></br>
                    </div>

                    <span className="p-float-label">
                        <InputText type="text" id="inputtext" value={newPassword.password || ''} style={{ width: '100%' }}
                            onChange={e => {
                                newPasswordSet({ ...newPassword, password: e.target.value });
                            }}
                        />
                        <label htmlFor="password">New password <span style={{ color: 'red' }}>*</span></label>
                    </span>
                    <br />
                    <span className="p-float-label">
                        <InputText type="text" id="inputtext" value={newPassword.confirmPassword || ''} style={{ width: '100%' }}
                            onChange={e => {
                                newPasswordSet({ ...newPassword, confirmPassword: e.target.value });
                            }}
                        />
                        <label htmlFor="password">Confirm password <span style={{ color: 'red' }}>*</span></label>
                    </span>
                </div>
            </Dialog>

        </div>
    );
};
export default AccountManagement;
