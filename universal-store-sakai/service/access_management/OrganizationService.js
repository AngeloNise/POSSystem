import createAxiosInstance from '../../util/CustomAxios'

export class OrganizationService {
    
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/system/menu";
    }

    showOrganizationTree(params) {
        return this.axiosInstance?.get(this.baseApi() + "/showOrganizationTree?companyId=" + this.userData?.COMPANY_ID);
    }

    saveNewOrg(params) {
        return this.axiosInstance?.post(this.baseApi() + "/saveNewOrg", params);
    }

    updateOrg(params) {
        return this.axiosInstance?.post(this.baseApi() + "/updateOrg", params);
    }

}
export default OrganizationService;