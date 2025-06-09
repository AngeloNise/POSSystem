'use client';
import React, { useState, useRef, useEffect } from 'react';
import { Accordion, AccordionTab } from 'primereact/accordion';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { FileUpload } from 'primereact/fileupload';
import { InputText } from 'primereact/inputtext';
import { Paginator } from 'primereact/paginator';
import { Ripple } from 'primereact/ripple';
import { ScrollPanel } from 'primereact/scrollpanel';
import { TabView, TabPanel } from 'primereact/tabview';
import { Toast } from 'primereact/toast';
import { Tree } from 'primereact/tree';

import { classNames } from 'primereact/utils';
import { useFormik } from 'formik';

import useAuthStore from '@/store/AuthStore';
import OrganizationService from '@/service/access_management/OrganizationService'
import { useRouter } from 'next/navigation';
import { Tag } from 'primereact/tag';

const OrganizationManagement = () => {
    const dataTable = useRef(null);
    const paginator = useRef(null);
    const toast = useRef(null);
    const regionDDLRef = useRef(null);

    const { userInfo } = useAuthStore();
    const orgService = new OrganizationService(userInfo);

    const [treeNodes, setTreeNodes] = useState([]);
    const [organizatioRes, setOrganizationRes] = useState([]);
    const [orgSelRes, setOrgSelRes] = useState(null);
    const [accounts, setAccounts] = useState([]);
    const [accSel, setAccSel] = useState([]);

    const [dialogOrg, setDialogOrg] = useState(false);

    const [actionType, setActionType] = useState("none");

    const [selCategoryType, selCategoryTypeSet] = useState(null);
    const [setDepartmentControl, setDepartmentControlSet] = useState(true);
    const [setRegionControl, setRegionControlSet] = useState(true);
    const [setAreaControl, setAreaControlSet] = useState(true);

    const [optionDepartmentList, optionDepartmentListSet] = useState([]);
    const [optionAreaList, optionAreaListSet] = useState([]);
    const [optionRegionList, optionRegionListSet] = useState([]);

    const [selRegionDDL, selRegionDDLSet] = useState(null);
    const [selAreaDDL, selAreaDDLSet] = useState(null);

    const optionCategoryList = [
        // {label: 'Department', code: '1'},
        { label: 'Department', code: '2' },
        { label: 'Region', code: '3' },
        { label: 'Area', code: '4' },
        { label: 'Project', code: '4' },
    ];

    useEffect(() => {
        queryOrgTree();
        selCategoryTypeSet(optionCategoryList[0]);
    }, []);

    const formik = useFormik({
        initialValues: {
            name: '',
            department: '1',
            region: '',
            orgType: '2',
            orgOldId: '',
            costCenter: '',
            wbs: '',
            area: ''
        },
        validate: (data) => {
            let errors = {
                name: "",
                department: "",
                region: "",
                area: ""
            };

            if (!data.name) {
                errors.name = "name is required";
            }

            if (data.orgType == '2') {
                if (!data.department) {
                    errors.department = "company is required";
                }
            }

            if (data.orgType == '3') {
                if (!data.department) {
                    errors.department = "company is required";
                }
                if (!data.region) {
                    errors.region = "region is required";
                }
            }

            if (data.orgType == '4') {
                if (!data.department) {
                    errors.department = "company is required";
                }
                if (!data.region) {
                    errors.region = "region is required";
                }
                if (!data.area) {
                    errors.area = "area is required";
                }
            }

            return errors;
        },
        onSubmit: (data) => {
            if (actionType == 'add') {
                orgService.saveNewOrg(data)
                    .then((response) => {
                        closeDialog();
                        queryOrgTree();
                    })
                    .catch((error) => {
                        const errMsg =
                            (error.response && error.response.data) || error.message || error.toString();
                        toast.current.show({ severity: 'error', summary: 'Org Page', detail: errMsg, life: 3000 });
                    });
            } else {
                data.orgOldId = orgSelRes.ORGANIZATION_ID;
                orgService.updateOrg(data).then((response) => {
                    if (response.data.status == 200) {
                        closeDialog();
                        queryOrgTree();
                    } else {
                        alert('error encountered');
                    }
                }).catch((error) => {
                    const errMsg =
                        (error.response && error.response.data) || error.message || error.toString();
                    toast.current.show({ severity: 'error', summary: 'Org Page', detail: errMsg, life: 3000 });
                })
            }
        }
    });

    /** MAIN */

    const onDepartmentInit = () => {
        let filterDept_ = treeNodes.filter(xo => xo.key == '1');
        let regionList_ = filterDept_[0].children;

        let regList_ = [];

        regionList_.map(xo => {
            let objxo_ = { label: xo.label, code: xo.key }
            regList_.push(objxo_);
        });

        optionRegionListSet(regList_);
    }

    const queryOrgTree = () => {
        orgService.showOrganizationTree()
            .then((response) => {
                setTreeNodes(response.data["orgTree"]);
                setOrganizationRes(response.data["queryOrig"]);
                setAccounts(response.data["queryAcc"]);
            })
            .catch((error) => {
                const errMsg =
                    (error.response && error.response.data) || error.message || error.toString();
                toast.current.show({ severity: 'error', summary: 'Org Page', detail: errMsg, life: 3000 });
            });
    }

    const closeDialog = () => {
        setDialogOrg(false);

        optionDepartmentListSet([]);
        optionAreaListSet([]);
        optionRegionListSet([]);

        formik.resetForm();
        formik.setErrors({});
    }

    /** HELPER */

    const orgTypeTemplate = (param) => {
        var orgTypeName = optionCategoryList[param?.type_ - 1];
        return orgTypeName?.label || '';
    }

    const isFormFieldValid = (name) => !!(formik.touched[name] && formik.errors[name]);
    const getErrorMessage = (param) => isFormFieldValid(param) ? formik.errors[param] : "";

    /** LISTENER */

    const onSelect = (event) => {
        const re = organizatioRes.filter(x => x.ORGANIZATION_ID == event.node.key);
        setOrgSelRes(re[0]);
        const reAcc = accounts.filter(y => y.orgid == event.node.key);
        setAccSel(reAcc);
    }

    const onChangeCategoryDDLV2 = (param) => {
        console.log(`onChangeCategoryDDLV2 | param: ${JSON.stringify(param)}`);
        selCategoryTypeSet(param);
        formik.setFieldValue('orgType', param?.code);
        //
        let depList_ = [];

        treeNodes.map(xo => {
            let objxo_ = { label: xo.label, code: xo.key }
            depList_.push(objxo_);
        });

        switch (param.code) {
            case '1':
                setDepartmentControlSet(true);
                setRegionControlSet(true);
                setAreaControlSet(true);

                optionDepartmentListSet([]);
                optionRegionListSet([]);
                optionAreaListSet([]);
                break;
            case '2':
                //setDepartmentControlSet(false);
                setRegionControlSet(true);
                setAreaControlSet(true);

                selRegionDDLSet(null);
                selAreaDDLSet(null);
                break;
            case '3':
                //setDepartmentControlSet(false);
                setRegionControlSet(false);
                setAreaControlSet(true);

                selRegionDDLSet(null);
                selAreaDDLSet(null);

                onDepartmentInit();
                break;
            case '4':
                //setDepartmentControlSet(false);
                setRegionControlSet(false);
                setAreaControlSet(false);

                selRegionDDLSet(null);
                selAreaDDLSet(null);

                onDepartmentInit();
                break;
        }
    }

    /** LAYOUT */

    const footerDialogOrg = (
        <div>
            <Button type="button" label="Dismiss" size="small" text
                onClick={() => closeDialog() } />
            <Button type="button" label="Save" className="p-button-primary"
                size="small" onClick={() => formik.handleSubmit() } />
        </div>
    );

    return (
        <div className="grid">
            <Toast ref={toast} position="bottom-right" />
            <div className="field col-12 md:col-4">
                <div className="card">
                    <h5>Organization Tree</h5>
                    <ScrollPanel style={{ width: '100%', height: '550px' }}>
                        <Tree value={treeNodes} onSelect={onSelect} selectionMode="single" />
                    </ScrollPanel>
                </div>
            </div>
            <div className="field col-12 md:col-8">
                <TabView >
                    <TabPanel header="Organization Details" >
                        <div className="template m-3">
                            <Button label="Add Organization" icon="pi pi-plus" size="small"
                                onClick={e => {
                                    setDialogOrg(true);
                                    setActionType("add");
                                }}>
                            </Button>
                        </div>
                        <div className="card">
                            <div className="p-fluid m-3">
                                <div className="field grid">
                                    <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Organization ID</label>
                                    <div className="col-12 md:col-9">
                                        <InputText id="email3" type="text" value={orgSelRes?.ORGANIZATION_ID || ''} disabled />
                                    </div>
                                </div>
                                <div className="field grid">
                                    <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Organization</label>
                                    <div className="col-12 md:col-9">
                                        <InputText id="email3" type="text" value={orgSelRes?.FULL_NAME || ''} disabled />
                                    </div>
                                </div>
                                <div className="field grid">
                                    <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Organization Type</label>
                                    <div className="col-12 md:col-9">
                                        <InputText id="email3" type="text" value={orgTypeTemplate(orgSelRes)} disabled />
                                    </div>
                                </div>
                                <div className="field grid">
                                    <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">Cost Center</label>
                                    <div className="col-12 md:col-9">
                                        <InputText id="email3" type="text" value={orgSelRes?.cost_center_ || ''} disabled />
                                    </div>
                                </div>
                                <div className="field grid">
                                    <label htmlFor="name3" className="col-12 mb-3 md:col-3 md:mb-0">WBS</label>
                                    <div className="col-12 md:col-9">
                                        <InputText id="email3" type="text" value={orgSelRes?.wbs_ || ''} disabled />
                                    </div>
                                </div>
                            </div>
                            <div className="field grid">
                                <Button label="Update" outlined size="small" disabled={!orgSelRes}
                                    onClick={e => {
                                        setDialogOrg(true);
                                        formik.setFieldValue("name", orgSelRes?.FULL_NAME || '');
                                        formik.setFieldValue("costCenter", orgSelRes?.cost_center_ || '');
                                        formik.setFieldValue("wbs", orgSelRes?.wbs_ || '');
                                        var selCateg_ = optionCategoryList[orgSelRes?.type_ - 2];
                                        onChangeCategoryDDLV2(selCateg_);
                                        setActionType("edit");
                                    }}
                                />
                            </div>
                        </div>
                        {/* <div className="template m-3">
                            <Button className="amazon p-0" aria-label="Amazon" disabled={!orgSelRes}
                                onClick={e => {
                                    setDialogOrg(true);
                                    formik.setFieldValue("name", orgSelRes?.FULL_NAME);
                                    formik.setFieldValue("costCenter", orgSelRes?.cost_center_);
                                    formik.setFieldValue("wbs", orgSelRes?.wbs_);
                                    var selCateg_ = optionCategoryList[orgSelRes?.type_ - 2];
                                    onChangeCategoryDDLV2(selCateg_);
                                    setActionType("edit");
                                }}>
                                <i className="pi pi-pencil px-2"></i>
                                <span className="px-3">Update </span>
                            </Button>
                        </div> */}
                    </TabPanel>

                    <TabPanel header="Organization User">
                        <DataTable value={accSel} paginator className="p-datatable-gridlines" rows={10}
                            dataKey="id" emptyMessage="No Accounts found." showGridlines>
                            <Column field="ACCOUNT_NAME" header="Account" style={{ minWidth: '12rem' }} />
                            <Column field="FULL_NAME" header="Name" style={{ minWidth: '12rem' }} />
                            <Column field="role" header="Role" style={{ minWidth: '12rem' }} />
                            <Column field="CREATE_DATE_F" header="Create Date" style={{ minWidth: '12rem' }} />
                        </DataTable>
                    </TabPanel>
                </TabView>
            </div>

            <Dialog header="Organization" visible={dialogOrg} style={{ width: '40vw' }} 
                modal footer={footerDialogOrg} onHide={() => closeDialog()}>
                <div className="card p-fluid">
                    <div className="grid">
                        <div className="col-3 ">Name:</div>
                        <div className="col-9">
                            <InputText value={formik.values.name} style={{ width: '100%' }}
                                name='name' onChange={formik.handleChange}
                                placeholder={getErrorMessage('name')}
                                className={classNames({ 'p-invalid': isFormFieldValid('name') })}
                            />
                        </div>

                        <div className="col-3 ">Select Hierarchy:</div>
                        <div className="col-9">
                            <Dropdown id="dropdown" options={optionCategoryList} value={selCategoryType} style={{ width: '100%' }} optionLabel="label"
                                onChange={(e) => { onChangeCategoryDDLV2(e.value) }}
                            />
                        </div>

                        <div className="col-3 hidden">Select Company:</div>
                        {/* <div className="col-9 hidden">
                                <Dropdown id="dropdown" options={optionDepartmentList} value={selDepartmentDDL} style={{ width: '100%' }} optionLabel="label"
                                    onChange={(e) => {
                                        selDepartmentDDLSet(e.value);
                                        //

                                        //formik.setFieldValue('department', e.value.code);

                                        // let filterDept_ = treeNodes.filter(xo => xo.key == e.value.code);
                                        // let regionList_ = filterDept_[0].children;

                                        // let regList_ = [];

                                        // regionList_.map(xo => {
                                        //     let objxo_ = {label: xo.label, code: xo.key}
                                        //     regList_.push(objxo_);
                                        // });

                                        // optionRegionListSet(regList_);
                                        //
                                    }}
                                    disabled={setDepartmentControl}

                                    name='department'
                                    placeholder={getErrorMessage('department')}
                                    className={classNames({ 'p-invalid': isFormFieldValid('department') })}
                                />
                            </div> */}

                        <div className="col-3 ">Select Region:</div>
                        <div className="col-9">
                            <Dropdown id="dropdown" options={optionRegionList} value={selRegionDDL} style={{ width: '100%' }} optionLabel="label"
                                ref={regionDDLRef}
                                onChange={e => {
                                    selRegionDDLSet(e.value);
                                    //alert(JSON.stringify(e.value));
                                    //
                                    formik.setFieldValue('region', e.value.code);
                                    //
                                    let filterDept_ = treeNodes.filter(xo => xo.key == '1');
                                    let regionList_ = filterDept_[0].children;
                                    //
                                    let filterArea_ = regionList_.filter(xx => xx.key == e.value.code);
                                    let areaListAss = filterArea_[0].children;

                                    let areaList_ = [];

                                    areaListAss.map(xo => {
                                        let objxo_ = { label: xo.label, code: xo.key }
                                        areaList_.push(objxo_);
                                    });
                                    //
                                    optionAreaListSet(areaList_);
                                }}
                                disabled={setRegionControl}

                                name='region'
                                placeholder={getErrorMessage('region')}
                                className={classNames({ 'p-invalid': isFormFieldValid('region') })}
                            />
                        </div>

                        <div className="col-3 ">Select Area:</div>
                        <div className="col-9">
                            <Dropdown id="dropdown" options={optionAreaList} value={selAreaDDL} style={{ width: '100%' }} optionLabel="label"
                                onChange={e => {

                                    selAreaDDLSet(e.value);

                                    formik.setFieldValue('area', e.value.code);
                                }}
                                disabled={setAreaControl}

                                name='area'
                                placeholder={getErrorMessage('area')}
                                className={classNames({ 'p-invalid': isFormFieldValid('area') })}
                            />
                        </div>

                        <div className="col-3 ">Cost Center:</div>
                        <div className="col-9">
                            <InputText value={formik.values.costCenter} style={{ width: '100%' }}
                                name='costCenter' onChange={formik.handleChange}
                            />
                        </div>

                        <div className="col-3 ">WBS:</div>
                        <div className="col-9">
                            <InputText value={formik.values.wbs} style={{ width: '100%' }}
                                name='wbs' onChange={formik.handleChange}
                            />
                        </div>
                    </div>
                </div>
            </Dialog>
        </div>
    );
};

export default OrganizationManagement;
