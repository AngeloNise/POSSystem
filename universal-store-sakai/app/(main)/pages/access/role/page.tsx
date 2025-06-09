'use client';
import React, { useState, useRef, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Divider } from 'primereact/divider';
import { Dropdown } from 'primereact/dropdown';
import { FilterMatchMode, FilterOperator } from 'primereact/api';
import { InputText } from 'primereact/inputtext';
import { SelectButton } from 'primereact/selectbutton';
import { TabPanel, TabView } from 'primereact/tabview';
import { Toast } from 'primereact/toast';
import { Tree } from 'primereact/tree';

import useAxiosInstance from '@/util/CustomAxios'
import useAuthStore from '@/store/AuthStore';
import RoleService from '@/service/access_management/RoleService';

const RoleManagement = () => {
    const dataTable = useRef(null);
    const paginator = useRef(null);
    const toast = useRef(null);

    const { userInfo } = useAuthStore();
    const roleService = new RoleService(userInfo);

    const [loadingTableRoles, setLoadingTableRoles] = useState(true); //setLoading1

    const [rolesList, setRolesList] = useState([]);
    const [resourcesList, setResourcesList] = useState([]);
    const [roleResourceList, setRoleResourcesList] = useState(null);
    const [accountList, setAccountList] = useState([]);

    const [resourceFilter, resourceFilterSet] = useState(null);
    const [acctFilters, setAcctFilters] = useState(null);

    const [roleModel, setRoleModel] = useState(null);
    const [actionType, setActionType] = useState('none');
    const [acctListFilt, setAcctListFilt] = useState([]);
    const [dialogResc, setDialogResc] = useState(false);
    const [resourcesOrig, setResourcesOrig] = useState([]);

    const [ddlResource, setDdlResource] = useState([]);
    const [selDdlResc, setSelDdlResc] = useState({});
    const [resourcesModel, setResourcesModel] = useState(null);
    const [selectedAccountId, selectedAccountIdSet] = useState(0);

    const [filters, setFilters] = useState({
        global: { value: null, matchMode: FilterMatchMode.CONTAINS }
    });
    const [globalFilterValue, setGlobalFilterValue] = useState('');

    const [displayBasic, setDisplayBasic] = useState(false);
    const [displayRole, setDisplayRole] = useState(false);
    const [dialogAcct, setDialogAcct] = useState(false);

    const [assignResourceBy, assignResourceBySet] = useState('');

    const [selectedRoleId, setSelectedRoleId] = useState(0);
    const [selectedKeys3, setSelectedKeys3] = useState(null);
    const [userDisplay, setUserDisplay] = useState(false);
    const [selectedAccounts, setSelectedAccounts] = useState([]);

    const [widgePermUser, widgePermUserSet] = useState([]);
    const [widgePermRole, widgePermRoleSet] = useState([]);

    const [btnPermSetup, btnPermSetupSet] = useState(false);
    const [btnItems, btnItemsSet] = useState([]);

    const [btnPermSelection, btnPermSelectionSet] = useState(null);
    const [userPermCurSel, userPermCurSelSet] = useState(null);


    const [loadingButtonSaveAssignResource, buttonLoadingSet] = useState(false);

    /** INIT */

    useEffect(() => {
        setLoadingTableRoles(true);
        queryAllRoles();
        initResourceFilter();
        initAccFilters();
    }, []);

    const initResourceFilter = () => {
        resourceFilterSet(
            {
                'global': { value: null, matchMode: FilterMatchMode.CONTAINS },
                'NAME_': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
                'CODE_': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
                'VALUE_': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
                'STATUS_': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
            }
        );
    }

    const initAccFilters = () => {
        setAcctFilters({
            'global': { value: null, matchMode: FilterMatchMode.CONTAINS },
            'FULL_NAME': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
            'ACCOUNT_NAME': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
            'role': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
            'organization': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
        });
    }

    /** MAIN */

    const queryAllRoles = () => {
        roleService.showAllRoles()
            .then((response) => {
                setRolesList(response.data["queryRoles"]);
                setResourcesList(response.data["treeResources"]);
                setRoleResourcesList(response.data["queryRoleResources"]);
                setAccountList(response.data["queryAccounts"]);
                setResourcesOrig(response.data["queryResources"]);
                widgePermUserSet(response.data["queryWidgeUser"]);
                widgePermRoleSet(response.data["queryWidgeRole"]);
                btnItemsSet(response.data["queryWidgeLegend"]);
                setLoadingTableRoles(false);
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Role Page', detail: errMsg, life: 3000 });
            });
    }

    const roleActivation = (id) => {
        const filtered = rolesList.filter(x => x.ROLE_ID == id);
        const params = { name: 'roleActivation', object: filtered[0], userId: userInfo.ACCOUNT_ID, userFullName: userInfo.FULL_NAME };

        roleService.roleActivation(params)
            .then((response) => {
                toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Update Success', life: 3000 });
                queryAllRoles();
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Role Page', detail: errMsg, life: 3000 });
            });
    }

    const saveAssignUser = () => {
        buttonLoadingSet(true);
        const params = { str: selectedAccountId, companyId: userInfo.COMPANY_ID, object: selectedKeys3 };

        roleService.saveAssignUser(params)
            .then((response) => {
                buttonLoadingSet(false);
                setDisplayBasic(false);
                toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Save Success', life: 3000 });
                queryAllRoles();
            })
            .catch((error) => {
                buttonLoadingSet(false);
                const errMsg =
                    (error.data && error.data.error) || (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Error saving', detail: errMsg, life: 3000 });
            });
    }

    const saveAssignRoles = () => {
        buttonLoadingSet(true);
        const params = {
            str: selectedRoleId,
            companyId: userInfo.COMPANY_ID,
            object: selectedKeys3
        };

        roleService.saveAssignRoles(params)
            .then((response) => {
                buttonLoadingSet(false);
                setDisplayBasic(false);
                toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Save Success', life: 3000 });
                queryAllRoles();
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'success', summary: 'Role Page', detail: errMsg, life: 3000 });
            });
    }

    const saveSelUsers = () => {
        const params = { name: 'saveSelUsers', str: selectedRoleId, object: selectedAccounts };

        roleService.saveSelUsers(params)
            .then((response) => {
                toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Assign User Success', life: 3000 });
                setUserDisplay(false);
                queryAllRoles();
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Role Page', detail: errMsg, life: 3000 });
            });
    }

    const removeUser = (accountID) => {
        const params = { name: "removeUser", str: accountID };
        roleService.removeUser(params)
            .then((response) => {
                toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Remove Success', life: 3000 });
                setSelectedAccounts(
                    selectedAccounts.filter(x => x.ACCOUNT_ID != accountID)
                );
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Role Page', detail: errMsg, life: 3000 });
            });
    }

    const actionRole = () => {
        const params = { name: "actionRole", action: actionType, object: roleModel, CREATED_BY_ID: userInfo.ACCOUNT_ID, CREATED_BY_NAME: userInfo.FULL_NAME };
        roleService.actionRole(params)
            .then((response) => {
                if (response.data.feedback === '1') {
                    queryAllRoles();
                    toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Save Success', life: 3000 });
                    setDisplayRole(false);
                } else {
                    toast.current.show({ severity: 'error', summary: 'Role Page', detail: 'Missing required fields', life: 5000 });
                }
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Role Page', detail: errMsg, life: 5000 });
            });
    }

    const saveNewResources = () => {
        const params = { name: "SaveNewResources", object: resourcesModel };
        roleService.saveNewResources(params)
            .then((response) => {
                if (response.data.feedback === '1') {
                    queryAllRoles();
                    toast.current.show({ severity: 'success', summary: 'Role Page', detail: 'Save Success', life: 3000 });
                    setDialogResc(false);
                } else {
                    toast.current.show({ severity: 'error', summary: 'Role Page', detail: 'Missing required fields', life: 5000 });
                }
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Role Page', detail: errMsg, life: 5000 });
            });
    }

    /** LISTENERS */

    const onGlobalFilterChange = (e) => {
        const value = e.target.value;
        let _filters = { ...filters };

        _filters['global'].value = value;

        setFilters(_filters);
        setGlobalFilterValue(value);
    };

    const onRowEditComplete = (e) => {
        let _resourcesOrig = [...resourcesOrig];
        let { newData, index } = e;
        _resourcesOrig[index] = newData;
        setResourcesOrig(_resourcesOrig);
        onRowUpdate(newData);
    }

    const onRowUpdate = (row) => {
        const params = { name: 'updateResourceByRow', object: row };
        roleService.updateResourceByRow(params)
            .then((response) => {
                toast.current.show({ severity: 'success', summary: 'Resource Page', detail: 'Update row Success', life: 3000 });
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Equiptment Page', detail: errMsg, life: 3000 });
            });
    }

    /** LAYOUTS */

    const headerTableRoles = () => {
        return <div>
            <div className="grid">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-start gap-2">
                        <Button label="Add" icon="pi pi-plus"
                            size="small" onClick={(e) => {
                                setDisplayRole(true);
                                setRoleModel({
                                    CREATED_BY_ID: userInfo.ACCOUNT_ID,
                                    CREATED_BY_NAME: userInfo.FULL_NAME,
                                });
                                setActionType("add");
                            }} />
                    </div>
                </div>
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                        <span className="p-input-icon-left">
                            <i className="pi pi-search" />
                            <InputText value={globalFilterValue} className="p-inputtext-sm" onChange={onGlobalFilterChange} placeholder="Search" />
                        </span>
                    </div>
                </div>
            </div>
        </div>
    };

    const stateLegend = (row) => {
        return <span className={`customer-badge status-${row.STATE == '1' ? 'qualified' : 'unqualified'}`}>
            {row.STATE == '1' ? 'ACTIVE' : 'INACTIVE'}
        </span>;
    }

    const actionBodyTemplate = (row) => {
        return (
            <div>
                <span>
                    <Button data-id={row.ROLE_ID} icon="pi pi-plus-circle" tooltip="Assign Resources"
                        className="p-button-outlined p-button-success mr-2 mb-2" size="small"
                        onClick={(e) => {
                            const element = (e.target as HTMLElement);
                            setDisplayBasic(true);
                            assignResourceBySet('ROLE');

                            const id = row.ROLE_ID;
                            setSelectedRoleId(row.ROLE_ID);
                            const single = roleResourceList.filter(x => id == x.ROLE_ID);
                            var obj = {};

                            // eslint-disable-next-line
                            single.map(y => {
                                obj[y.RESOURCES_ID] = { checked: true, partialChecked: false };
                            });

                            setSelectedKeys3(obj);
                        }} />

                    <Button data-id={row.ROLE_ID} icon="pi pi-users" tooltip="Assign Users"
                        className="p-button-outlined p-button-warning mr-2 mb-2" size="small"
                        onClick={(e) => {
                            setUserDisplay(true);

                            const id = row.ROLE_ID;
                            setSelectedAccounts(accountList.filter(y => id == y.role_id));
                            setSelectedRoleId(id);
                        }} />

                    <Button data-id={row.ROLE_ID} icon="pi pi-sort-alt" tooltip={row.STATE === '1' ? 'disable' : 'enable'}
                        className={`p-button-outlined p-button-${row.STATE == '1' ? 'danger' : 'info'} mr-2 mb-2`} size="small"
                        onClick={(e) => {
                            const id = row.ROLE_ID;
                            roleActivation(id);
                        }} />

                    <Button icon="pi pi-pencil" tooltip="Role Update" size="small"
                        className="p-button-outlined p-button-warning mr-2 mb-2"
                        onClick={e => {
                            setDisplayRole(true);
                            const single = rolesList.find(x => x.ROLE_ID === row.ROLE_ID);
                            setRoleModel({
                                ...single,
                                CREATED_BY_ID: userInfo.ACCOUNT_ID,
                                CREATED_BY_NAME: userInfo.FULL_NAME
                            });
                            setActionType("edit");
                        }} />


                    <Button icon="pi pi-file-edit" tooltip="Edit Permission" size="small"
                        className="p-button-outlined p-button-success mr-2 mb-2"
                        onClick={e => {
                            btnPermSelectionSet([]);

                            let userPermCurSel_ = widgePermRole.filter(x => x.roleid == row.ROLE_ID);

                            if (userPermCurSel_.length > 0) {
                                userPermCurSel_.map(xo => {
                                    xo.objectArr.map(xx => {
                                        let display_ = xx.display;
                                        if (display_ != 'hidden') {
                                            btnPermSelectionSet(ox => [...ox, xx.widget_code]);
                                        }

                                    });
                                });
                            }
                            //
                            userPermCurSelSet({ roleid: row.ROLE_ID, resourceid: '45' });
                            //
                            btnPermSetupSet(true);
                            //
                            setActionType('permRole');
                        }} />
                </span>
            </div>
        );
    }

    const footerDialogRoleModule = (
        <div>
            <Button type="button" label="Dismiss" text size="small"
                onClick={() => setDisplayRole(false)} />
            <Button type="button" label="Save" className="p-button-primary"
                size="small" onClick={e => actionRole()} />
        </div>
    );

    const footerDialogResourceList = (
        <div>
            <Button type="button" label="Dismiss" text size="small"
                onClick={() => setDisplayBasic(false)} />
            {
                assignResourceBy === 'ROLE' ?
                    <>
                        <Button type="button" label="Save To Role" className="p-button-primary"
                            loading={loadingButtonSaveAssignResource} size="small"
                            onClick={() => saveAssignRoles()}
                        />
                    </>
                    : <>
                        <Button type="button" label="Save To User" className="p-button-primary"
                            loading={loadingButtonSaveAssignResource} size="small"
                            onClick={() => saveAssignUser()}
                        />
                    </>
            }
        </div>
    );

    const footerDialogUserList = (
        <div>
            <Button type="button" label="Dismiss" text
                size="small" onClick={() => setUserDisplay(false)} />
            <Button type="button" label="Save" className="p-button-primary"
                size="small" onClick={() => saveSelUsers()} />
        </div>
    );

    const headerTableResources = () => {
        return <div>
            <div className="grid">
                <div className="col-6 md:col-6">
                    <div className="flex flex-wrap align-items-center justify-content-start gap-2">
                        <Button label="Add" icon="pi pi-plus" size="small"
                            onClick={(e) => {
                                setDialogResc(true);
                                const rescType1 = resourcesOrig.filter(x => x.TYPE_ == '1');
                                var parent = {};
                                setDdlResource(
                                    rescType1.map(item => {
                                        return { ...parent, name: item.NAME_, id: item.RESOURCES_ID }
                                    })
                                );
                                setResourcesModel({});
                                setSelDdlResc({});
                            }} />
                    </div>
                </div>
                <div className="col-6 md:col-6">
                    {/* <div className="flex flex-wrap align-items-center justify-content-end gap-2">
                        <span className="p-input-icon-left">
                            <i className="pi pi-search" />
                            <InputText value={globalFilterValue} onChange={onGlobalFilterChange} placeholder="Search" />
                        </span>
                    </div> */}
                </div>
            </div>
        </div>
    };

    const footerDialogResourceModule = (
        <div>
            <Button type="button" label="Dismiss" size="small" text
                onClick={() => setDialogResc(false)} />
            <Button type="button" label="Save" className="p-button-primary" size="small"
                onClick={() => saveNewResources()} />
        </div>
    );

    const actionAcctOriginTemplate = (row) => {
        return (
            <div>
                <span>
                    <Button data-id={row.ACCOUNT_ID} icon="pi pi-times" className="p-button-outlined p-button-danger mr-2 mb-2"
                        tooltip="Remove" size="small" onClick={e => { removeUser(row.ACCOUNT_ID) }} />
                </span>
            </div>
        );
    }

    const actionAcctTemplate = (row) => {
        return (
            <div>
                <span>
                    <Button data-id={row.ACCOUNT_ID} className="mr-2 mb-2" text
                        label="select"
                        onClick={e => {
                            setDialogAcct(false);
                            const selAccount = acctListFilt.filter(x => x.ACCOUNT_ID == row.ACCOUNT_ID);
                            const sel2Account = { ...selAccount[0], isNew: 'true' }
                            setSelectedAccounts([...selectedAccounts, sel2Account]);
                        }}
                    />
                </span>
            </div>
        );
    }

    const footerDialogButtonPerms = (
        <>
            <Button type="button" label="Dismiss" text
                onClick={e => btnPermSetupSet(false)} />
            <Button type="button" label="Save" className="p-button-primary"
                size="small" onClick={e => {
                    let params = {};

                    if (actionType == 'permUser') {
                        params = { userid: userPermCurSel.userid, companyId: userInfo.COMPANY_ID, resourceid: userPermCurSel.resourceid, widgelist: btnPermSelection, type: actionType };
                    } else {
                        params = { roleid: userPermCurSel.roleid, companyId: userInfo.COMPANY_ID, resourceid: userPermCurSel.resourceid, widgelist: btnPermSelection, type: actionType };
                    }

                    roleService.btnPermissionManager(params)
                        .then((response) => {
                            toast.current.show({ severity: 'success', summary: 'Button Permission', detail: 'Update Success', life: 3000 });
                            btnPermSetupSet(false);
                            queryAllRoles();
                        })
                        .catch((error) => {
                            toast.current.show({ severity: 'error', summary: 'Error', detail: 'Update Success', life: 3000 });
                        });
                }} />
        </>
    );

    const textEditor = (options) => {
        return <InputText type="text" value={options.value} onChange={(e) => options.editorCallback(e.target.value)} />;
    }

    const headerUserPage = () => {
        return (
            <div className="flex justify-content-end">
                <span className="p-input-icon-left">
                    <i className="pi pi-search" />
                    <InputText value={globalFilterValue} onChange={onGlobalFilterChange}
                        className="p-inputtext-sm" placeholder="User Account Search" />
                </span>
            </div>
        );
    };

    const actionAccountTemplate = (row) => {
        return (
            <div>
                <span>
                    <Button data-id={row.ACCOUNT_ID} icon="pi pi-plus-circle" className="p-button-outlined p-button-success mr-2 mb-2"
                        tooltip="Assign Resources" size="small"
                        onClick={(e) => {
                            setDisplayBasic(true);
                            assignResourceBySet('USER');

                            const id = row.ACCOUNT_ID;//e.target.dataset.id;
                            selectedAccountIdSet(id);
                            const single = roleResourceList.filter(x => x.USE_TYPE == 2 && x.USER_ID == id);
                            var obj = {};

                            single.map(y => {
                                obj[y.RESOURCES_ID] = { checked: true, partialChecked: false };
                            });

                            setSelectedKeys3(obj);
                        }} />

                    <Button icon="pi pi-file-edit" className="p-button-outlined p-button-success mr-2 mb-2"
                        tooltip="Edit Permission" size="small"
                        onClick={e => {
                            btnPermSelectionSet([]);
                            let userPermCurSel_ = widgePermUser.filter(x => x.userid == row.ACCOUNT_ID);

                            if (userPermCurSel_.length > 0) {
                                userPermCurSel_.map(xo => {
                                    xo.objectArr.map(xx => {
                                        let display_ = xx.display;
                                        if (display_ != 'hidden') {
                                            btnPermSelectionSet(ox => [...ox, xx.widget_code]);
                                        }

                                    });
                                });
                            }
                            userPermCurSelSet({ userid: row.ACCOUNT_ID, resourceid: '45' });
                            btnPermSetupSet(true);
                            setActionType('permUser');
                        }} />
                </span>
            </div>
        );
    }

    const genderBodyTemplate = (param) => {
        return  <span className={`customer-badge status-${param.SEX == '1' ? 'new' : 'unqualified'}`}>
        {param.SEX == '1' ? 'MALE' : 'FEMALE'}
        </span>;
    }

    const stateBodyTemplate = (param) => {
        return  <span className={`customer-badge status-${param.STATE == '1' ? 'qualified' : 'unqualified'}`}>
        {param.STATE == '1' ? 'ACTIVE' : 'INACTIVE'}
        </span>;
    }

    //Roles Page, Resource Page, User Page
    return (
        <div className="grid">
            <Toast ref={toast} position="bottom-right" />
            <div className="col-12">
                <div className="card">
                    <TabView>
                        <TabPanel header="Roles Page">
                            <DataTable value={rolesList} paginator className="p-datatable-gridlines" showGridlines rows={10}
                                dataKey="id" filters={filters} filterDisplay="menu" loading={loadingTableRoles} responsiveLayout="scroll"
                                emptyMessage="No customers found." rowsPerPageOptions={[10, 20, 50]} sortMode="multiple"
                                currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
                                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                editMode="row" header={headerTableRoles} globalFilterFields={['NAME_']}>
                                <Column header="ACTION" body={actionBodyTemplate} style={{ minWidth: '17rem' }}></Column>
                                <Column field="ROLE_ID" header="Role ID" style={{ minWidth: '5rem' }} />
                                <Column field="NAME_" header="Role Name" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                                <Column field="DESCRIPTION" header="Description" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                                <Column field="STATE" header="State" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} body={stateLegend} />
                                <Column field="CREATE_DATE_F" header="Create date" style={{ minWidth: '12rem' }} />
                            </DataTable>
                        </TabPanel>

                        <TabPanel header="Resources Page">
                            <DataTable value={resourcesOrig} className="p-datatable-gridlines"
                                dataKey="id" responsiveLayout="scroll" sortMode="multiple"
                                emptyMessage="No customers found." header={headerTableResources}
                                paginator rowsPerPageOptions={[8, 20, 50]} showGridlines rows={8}
                                currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
                                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                editMode="row" onRowEditComplete={onRowEditComplete}
                                filters={resourceFilter} >
                                <Column rowEditor headerStyle={{ width: '10%', minWidth: '4rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                                <Column field="NAME_" header="Name" filter style={{ minWidth: '12rem' }} editor={(options) => textEditor(options)} />
                                <Column field="CODE_" header="Code" filter style={{ minWidth: '12rem' }} />
                                <Column field="TYPE_" header="Type" style={{ minWidth: '12rem' }} />
                                <Column field="VALUE_" header="Link" filter style={{ minWidth: '12rem' }} editor={(options) => textEditor(options)} />
                                <Column field="CREATE_DATE_F" header="Create date" style={{ minWidth: '12rem' }} />
                                <Column field="ICON_URL" header="Icon code" style={{ minWidth: '12rem' }} editor={(options) => textEditor(options)} />
                                <Column field="STATUS_" header="Status" filter style={{ minWidth: '12rem' }} editor={(options) => textEditor(options)} />
                            </DataTable>
                        </TabPanel>

                        <TabPanel header="User Page">
                            <DataTable value={accountList} className="p-datatable-gridlines"
                                dataKey="id" responsiveLayout="scroll"
                                emptyMessage="No customers found."
                                paginator rowsPerPageOptions={[8, 20, 50]} showGridlines rows={8}
                                currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
                                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                filters={filters} header={headerUserPage} globalFilterFields={['ACCOUNT_NAME']} >
                                <Column header="ACTION" body={actionAccountTemplate} style={{ minWidth: '4rem' }} />
                                <Column field="ACCOUNT_NAME" header="Account" filter style={{ minWidth: '12rem' }} />
                                <Column field="FULL_NAME" header="Name" filter style={{ minWidth: '12rem' }} />
                                <Column field="MOBILE_PHONE_A" header="Contact#" filter style={{ minWidth: '12rem' }} />
                                <Column field="SEX" header="Gender" style={{ minWidth: '12rem' }} body={genderBodyTemplate} />
                                <Column field="STATE" header="State" style={{ minWidth: '12rem' }} body={stateBodyTemplate} />
                                <Column field="CREATE_DATE_F" header="Create Date" style={{ minWidth: '12rem' }} />
                            </DataTable>
                        </TabPanel>
                    </TabView>

                    <Dialog header="Resources List" visible={displayBasic} className="resources-dialog"
                        modal footer={footerDialogResourceList} onHide={() => setDisplayBasic(false)}>
                        <Tree value={resourcesList} selectionMode="checkbox" selectionKeys={selectedKeys3}
                            onSelectionChange={e => {
                                setSelectedKeys3(e.value);
                            }} />
                    </Dialog>

                    <Dialog header="User List" visible={userDisplay} style={{ width: '70vw' }} onHide={() => setUserDisplay(false)} modal footer={footerDialogUserList}>
                        <Button label="Add User" icon="pi pi-user-plus" className="p-button-sm mb-4"
                            onClick={e => {
                                setDialogAcct(true);
                                const questionssData = accountList.filter(each => !selectedAccounts.includes(each));
                                setAcctListFilt(questionssData);
                            }} />
                        <DataTable value={selectedAccounts} paginator className="p-datatable-gridlines" showGridlines rows={10}
                            dataKey="id" filters={acctFilters} filterDisplay="menu" responsiveLayout="scroll"
                            emptyMessage="No Account found.">
                            <Column header="ACTION" body={actionAcctOriginTemplate} style={{ minWidth: '5rem' }}></Column>
                            <Column field="FULL_NAME" header="Full Name" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                            <Column field="ACCOUNT_NAME" header="Account" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                            <Column field="role" header="Role" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                            <Column field="organization" header="Organization" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                        </DataTable>
                    </Dialog>

                    <Dialog header="Roles module" visible={displayRole} className="resources-dialog" modal footer={footerDialogRoleModule} onHide={() => setDisplayRole(false)}>
                        <div className="card p-fluid">
                            <div className="field grid">
                                <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Name &nbsp;
                                    <span style={{ color: 'red' }}>*</span> </label>
                                <div className="col-12 md:col-9">
                                    <InputText id="email3" type="text" value={roleModel?.NAME_ || ''}
                                        onChange={e => setRoleModel({ ...roleModel, NAME_: e.target.value.toUpperCase() })} />
                                </div>
                            </div>
                            <div className="field grid">
                                <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Description &nbsp;
                                </label>
                                <div className="col-12 md:col-9">
                                    <InputText id="email3" type="text" value={roleModel?.DESCRIPTION || ''}
                                        onChange={e => setRoleModel({ ...roleModel, DESCRIPTION: e.target.value.toUpperCase() })} />
                                </div>
                            </div>
                        </div>
                    </Dialog>

                    <Dialog header="User module" visible={dialogAcct} style={{ width: '60vw' }} modal onHide={() => setDialogAcct(false)}>
                        <DataTable value={acctListFilt} paginator className="p-datatable-gridlines" showGridlines rows={10}
                            dataKey="id" filters={acctFilters} filterDisplay="menu" responsiveLayout="scroll"
                            emptyMessage="No Account found.">
                            <Column header="ACTION" body={actionAcctTemplate} style={{ minWidth: '10rem' }}></Column>
                            <Column field="FULL_NAME" header="Full Name" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                            <Column field="ACCOUNT_NAME" header="Account" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                            <Column field="role" header="Role" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                            <Column field="organization" header="Organization" filter filterPlaceholder="Search by name" style={{ minWidth: '12rem' }} />
                        </DataTable>
                    </Dialog>

                    <Dialog header="Resource module" visible={dialogResc} className="resmodule-dialog" modal onHide={() => setDialogResc(false)} footer={footerDialogResourceModule}>
                        <div className="card p-fluid">
                            <div className="field grid">
                                <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Name &nbsp;
                                    <span style={{ color: 'red' }}>*</span> </label>
                                <div className="col-12 md:col-9">
                                    <InputText id="email3" type="text" onChange={e => setResourcesModel({ ...resourcesModel, NAME_: e.target.value })}
                                        value={resourcesModel?.NAME_ || ''} />
                                </div>
                            </div>

                            <div className="field grid">
                                <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Link &nbsp;
                                    <span style={{ color: 'red' }}>*</span> </label>
                                <div className="col-12 md:col-9">
                                    <InputText id="email3" type="text" onChange={e => setResourcesModel({ ...resourcesModel, LINK: e.target.value })}
                                        value={resourcesModel?.LINK || ''} />
                                </div>
                            </div>

                            <div className="field grid">
                                <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Icon menu &nbsp;
                                    <span style={{ color: 'red' }}>*</span> </label>
                                <div className="col-12 md:col-9">
                                    <InputText id="email3" type="text" onChange={e => setResourcesModel({ ...resourcesModel, ICON_URL: e.target.value })}
                                        value={resourcesModel?.ICON_URL || ''} />
                                </div>
                            </div>

                            <div className="field grid">
                                <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Parent

                                </label>
                                <div className="col-12 md:col-9">
                                    <Dropdown id="dropdown" style={{ width: '100%' }} options={ddlResource} value={selDdlResc}
                                        onChange={e => {
                                            setSelDdlResc(e.value);
                                            var val = e.value.id;
                                            setResourcesModel({ ...resourcesModel, PARENTID: val });
                                        }}
                                        optionLabel="name">
                                    </Dropdown>
                                </div>
                            </div>
                        </div>
                    </Dialog>

                    <Dialog header="Button Permission Setup" visible={btnPermSetup} className="button-permission" modal
                        onHide={() => btnPermSetupSet(false)} footer={footerDialogButtonPerms}>
                        <div className="grid">
                            <div className="col-12">
                                <div className="flex align-items-center justify-content-center mt-2">
                                    <SelectButton
                                        optionLabel="name" options={btnItems} multiple
                                        /* onContextMenu={(e) => cm.current.show(e)} */
                                        value={btnPermSelection} onChange={e => {
                                            btnPermSelectionSet(e.value);
                                        }} />
                                </div>
                            </div>
                            <Divider type="solid" />
                        </div>
                    </Dialog>
                </div>
            </div>
        </div>
    );
};

export default RoleManagement;
