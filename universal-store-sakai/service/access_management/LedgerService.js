import createAxiosInstance from '../../util/CustomAxios'

export class LedgerService {

    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/ledger";
    }

    getAllTransaction() {
        return this.axiosInstance?.get(this.baseApi() +`/transaction`);
    }

    onSearch(params) {
        return this.axiosInstance?.post(this.baseApi() + "/onChangePage" , params);
    }



    transactionsUpdate(params) {
        return this.axiosInstance?.post(this.baseApi() + "/transactionUpdate", params);
    }

}
export default LedgerService;
