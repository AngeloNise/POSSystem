import createAxiosInstance from "../../util/CustomAxios";

export class PurchaseService {
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/api/transaction";
    }

    list(params) {
        return this.axiosInstance.post(this.baseApi() + "/list", params);
    }

    getLatestReceiptNumber() {
        return this.axiosInstance?.post(this.baseApi() + "/latest-receipt-number");
    }

    finalizeTransaction(receiptData) {
        return this.axiosInstance?.post(this.baseApi() + "/finalize", receiptData)
            .then(response => {
                return response;
            });
    }

    searchSerialNumbers(productId, keyword) {
        return this.axiosInstance.post(this.baseApi() + "/serialnumber-search", {
            productId,
            keyword
        });
    }

}

export default PurchaseService;
