import createAxiosInstance from '../../util/CustomAxios'

export class TestProductService {

    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/test";
    }

    getAllProducts() {
        return this.axiosInstance?.get(this.baseApi() +`/products`);
    }

    onSearch(params) {
        return this.axiosInstance?.post(this.baseApi() + "/search" , params);
    }

    getAllTableCount() {
        return this.axiosInstance?.get(this.baseApi() +`/tableCount`);
    }
    productsUpdate(params) {
        return this.axiosInstance?.post(this.baseApi() + "/productsUpdate", params);
    }
}

export default TestProductService;
