import createAxiosInstance from '../../util/CustomAxios';

export class StoreService {
    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;
    }

    baseApi() {
        return "/api/store";
    }

    getStore(id) {
        return this.axiosInstance.post(`${this.baseApi()}/get`, { id });
    }

    saveStore(data) {
        return this.axiosInstance.post(`${this.baseApi()}/save`, data);
    }

    updateStore(data, file) {
        if (file) {
            const formData = new FormData();
            // Append each key of data to formData
            Object.keys(data).forEach(key => formData.append(key, data[key]));
            formData.append('logoFile', file);
            return this.axiosInstance.post(`${this.baseApi()}/update`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
        }
        return this.axiosInstance.post(`${this.baseApi()}/update`, data);
    }

    uploadLogo(file) {
        const formData = new FormData();
        formData.append('logo', file);
        return this.axiosInstance.post(`${this.baseApi()}/upload-logo`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    }
}

export default StoreService;
