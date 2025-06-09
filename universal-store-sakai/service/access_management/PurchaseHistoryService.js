import createAxiosInstance from '../../util/CustomAxios';

export class PurchaseHistoryService {
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/api/purchase-record";
    }

    list(params) {
        return this.axiosInstance.post(this.baseApi() + "/list", params);
    }

    delete(id) {
        return this.axiosInstance.delete(this.baseApi() + `/delete/${id}`);
    }

    getStore(id) {
        return this.axiosInstance.post(`${this.baseApi()}/get`, { id });
    }

}
export default PurchaseHistoryService;
