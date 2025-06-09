import createAxiosInstance from '../../util/CustomAxios'

export class SystemService {
    
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/system/menu";
    }

    showMenuResources() {
        return this.axiosInstance?.get(this.baseApi() + "/showMenuResources?role=" + this.userData?.ROLE_NAME +
            "&username=" + this.userData?.ACCOUNT_NAME + "&companyId=" + this.userData?.COMPANY_ID);
    }
}

export default SystemService;