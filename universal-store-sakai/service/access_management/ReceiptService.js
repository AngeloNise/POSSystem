import createAxiosInstance from '../../util/CustomAxios'


export  class ReceiptService{
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }


    baseApi() {
        return "/receipt";
    }

    getAllTransaction() {
        return this.axiosInstance?.get(this.baseApi() +`/receiptData`);
    }

    receiptsUpdate(params) {
        return this.axiosInstance?.post(this.baseApi() + "/receiptsUpdate", params);
    }

}

export default ReceiptService;
