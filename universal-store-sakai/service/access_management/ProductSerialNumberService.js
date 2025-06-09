import createAxiosInstance from '../../util/CustomAxios'

export  class  ProductSerialNumberService{

    constructor(userData) {
        this.axiosInstance = createAxiosInstance()
        this.userData = userData;
    }


    baseApi() {
        return "/inventory";
    }


    showSerialNumbers(start, limit, productId, debounce){
        const payLoad = {
            productId: productId,
            serialTerm: debounce,
            start: start,
            limit: limit
        }
        return this.axiosInstance.post(this.baseApi() + "/showSerialNumbers", payLoad)
            .then(res => res?.data)
            .catch(err => {
                console.error("Get products error:", err);
                throw err;
            });
    }


    addSerialNumbers(productId, serialNumbers){
        const payLoad ={
            productId: productId,
            serialNumber: serialNumbers
        }
        return this.axiosInstance.post(this.baseApi() + "/addProductSerial", payLoad)
            .then(res => res?.data)
            .catch(err => {
                console.error("Get products error:", err);
                throw err;
            });
    }

    deleteSerialNumbers(productId, serialNumbers){
        const payLoad = {
            productId: productId,
            serialNumber: serialNumbers
        }
        return this.axiosInstance.post(this.baseApi() + "/deleteSerialNumbers", payLoad)
            .then(res => res?.data)
            .catch(err =>{
                console.error("delete serial eerror" , err)
                throw err
            })
    }

}

export default ProductSerialNumberService;
