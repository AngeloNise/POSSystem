import createAxiosInstance from '../../util/CustomAxios'

export class AccountService {

    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/system/account";
    }

    showAllAccountsByOrg() {
        const objectV = {name:'showAllAccounts', conditions: null , action: null , object: this.userData};
        return this.axiosInstance?.post(this.baseApi() + "/showAllAccountsByOrg", objectV);
    }

    accountActivation(params) {
        return this.axiosInstance?.post(this.baseApi() + "/accountActivation", params);
    }

    accountsUpdate(params) {
        return this.axiosInstance?.post(this.baseApi() + "/accountsUpdate", params);
    }

    viewSignature(path) {
        return this.axiosInstance?.get(this.baseApi() + "/viewSignature?signaturePath=" + path);
    }

    getRoleOrgListByUserid(userId) {
        return this.axiosInstance?.post(this.baseApi() + "/getRoleOrgListByUserid", {"userid": userId});
    }

    getWidgetPermission(params) {
        return this.axiosInstance?.post(this.baseApi() + "/getWidgetPermission", params);
    }

    switchSourceMenuSave(params) {
        return this.axiosInstance?.post(this.baseApi() + "/SwitchSourceMenuSave", params);
    }

    uploadNewUserFromExcel(formData) {
        return this.axiosInstance?.post(this.baseApi() + "/uploadNewUserFromExcel", formData);
    }

    editUserPassword(params) {
        return this.axiosInstance?.post(this.baseApi() + "/editUserPassword", params);
    }
    updateResourceByRow(params) {
        return this.axiosInstance?.post(this.baseApi() + "/updateResourceByRow", params);
    }

}

export default AccountService;
