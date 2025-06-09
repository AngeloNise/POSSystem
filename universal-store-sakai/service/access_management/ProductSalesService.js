import createAxiosInstance from '../../util/CustomAxios';

export class ProductSalesService {
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    BaseApi() {
        return "/api/dashboard";
    }

    dashboardList(params) {
        return this.axiosInstance.post(this.BaseApi() + "/list", params);
    }

    recentList(params) {
        return this.axiosInstance.post(this.BaseApi() + "/recentlist", params);
    }

    getMetricsByPeriod(period) {
        return this.axiosInstance.post(`${this.BaseApi()}/metrics`, { period });
    }
}

export default ProductSalesService;
