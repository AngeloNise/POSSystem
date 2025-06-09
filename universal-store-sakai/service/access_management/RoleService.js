import createAxiosInstance from '../../util/CustomAxios'

export class RoleService {
    
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/system/role";
    }

    showAllRoles() {
        return this.axiosInstance?.get(this.baseApi() + "/showAllRoles?companyId=" + this.userData.COMPANY_ID);
    }

    roleActivation(params) {
        return this.axiosInstance?.post(this.baseApi() + "/roleActivation", params);
    }

    saveAssignRoles(params) {
        return this.axiosInstance?.post(this.baseApi() + "/saveAssignRoles", params);
    }

    saveAssignUser(params) {
        return this.axiosInstance?.post(this.baseApi() + "/saveAssignUser", params);
    }

    saveSelUsers(params) {
        return this.axiosInstance?.post(this.baseApi() + "/saveSelUsers", params);
    }

    removeUser(params) {
        return this.axiosInstance?.post(this.baseApi() + "/removeUser", params);
    }

    actionRole(params) {
        return this.axiosInstance?.post(this.baseApi() + "/actionRole", params);
    }

    btnPermissionManager(params) {
        return this.axiosInstance?.post(this.baseApi() + "/btnPermissionManager", params);
    }

    updateResourceByRow(params) {
        return this.axiosInstance?.post(this.baseApi() + "/updateResourceByRow", params);
    }

    saveNewResources(params) {
        return this.axiosInstance?.post(this.baseApi() + "/saveNewResources", params);
    }
}

export default RoleService;