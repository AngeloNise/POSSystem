import createAxiosInstance from '../../util/CustomAxios';

export class SupplierService {
    constructor() {
        this.axiosInstance = createAxiosInstance();
    }

    baseUrl() {
        return window.location.protocol + "//" + window.location.hostname + ":8090";
    }

    baseApi() {
        return this.baseUrl() + "/api/supplier";
    }

    list(params) {
        return this.axiosInstance.post(this.baseApi() + "/list", params);
    }

    import(file) {
        const formData = new FormData();
        formData.append('file', file); // Append the file to the FormData

        // Use the axios instance to send the POST request with the file
        return this.axiosInstance.post(this.baseApi() + "/import", formData, {
            headers: {
                'Content-Type': 'multipart/form-data', // Important for file uploads
            },
        });
    }
}

export default SupplierService;
