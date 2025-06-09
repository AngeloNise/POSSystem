import createAxiosInstance from '../../util/CustomAxios'
import axios from "axios";
import {format} from "date-fns";

export class ProductService {

    constructor(userData) {
        this.axiosInstance = createAxiosInstance();
        this.userData = userData;

    }


    baseApi() {
        return "/inventory";
    }

    onSearchProduct(params) {
        return this.axiosInstance?.post(this.baseApi() + "/onSearchProduct" , params);
    }

    onProductAction(params) {
        return this.axiosInstance?.post(this.baseApi() + "/onAddEditProductInfo" , params);
    }



    // add or edit
    productActionInfo(productInfo, userInfo, selectedType, actionType, serialNumbers, img) {
        let serialize = false;
        if(selectedType === "YES"){
            serialize = true;
        }else{
            serialize = false;
        }


        const payload = {
            accountId: userInfo.ACCOUNT_ID,
            id: productInfo.id,
            action: actionType,
            capital: productInfo.capital,
            serialize: serialize,
            productName: productInfo.productName,
            variation: productInfo.variation,
            profit: productInfo.profit,
            stocks: productInfo.stocks,
            createBy: userInfo.FULL_NAME,
            modifiedBy: userInfo.FULL_NAME,
            serialData : {
                productId: productInfo.id,
                serialNumber : serialNumbers

            }
        };
        const formData = new FormData();
        formData.append("productImage", img?.[0] ?? null);
        formData.append("productInfo", new Blob([JSON.stringify(payload)], { type: "application/json" }));
        return this.onProductAction(formData)
            .then(res => res?.data)
            .catch(err => {
                console.error("Search error:", err);
                throw err;
            });
    }

    // get product method
    onDisplayProducts(start, limit, queryParams, createdDate){
        let dateCreated="";
        if(createdDate !== null) {
            dateCreated = format(createdDate, 'yyyy-MM-dd');
        }
        const payLoad = {
            start: start,
            limit: limit,
            query: {
                serialNumber: queryParams?.serial_number,
                productName: queryParams?.name,
                variation: queryParams?.variation,
                modifiedBy: queryParams?.modified_by,
                createdBy: queryParams?.created_by,
                createdDate: dateCreated

            }
        }
        return this.axiosInstance?.post(this.baseApi() + "/getProductsList" , payLoad)
            .then(res => res?.data)
            .catch(err => {
                console.error("Get products error:", err);
                throw err;
            });
    }


    onDeleteProduct(productId){
        const payLoad = {
            productId: productId
        }
        return this.axiosInstance?.post(this.baseApi() + "/onDeleteProduct", payLoad)
            .then(res => res?.data)
            .catch(err => {

                throw err;
            });
    }


    showSerialNumbers(id , inputSearchValue){
        const payLoad = {
            serialValue: inputSearchValue,
            productId: id
        }
        return this.axiosInstance?.post(this.baseApi() + "/onShowSerials" , payLoad)
            .then((res) => {
                const response = res.data.serialNumbers
                return response
            })
            .catch(err => {
                console.error("Get products error:", err);
                throw err;
            });
    }

    onDeleteSerialNumber(serial){
        const payLoad = {
            serialNumber: serial
        }
        return this.axiosInstance?.post(this.baseApi() + "/onDeleteSerialNumber", payLoad)
            .then((res) => {

            })
            .catch(err => {
                throw err;
            });
    }


    onAddSerialNumber(productId, serialNumber){
        const payLoad = {
            productId: productId,
            serialNumber: serialNumber
        }
        return this.axiosInstance?.post(this.baseApi() + "/onAddSerialNumber", payLoad)
            .then((res) => {

            })
            .catch(err => {

                throw err;

            });
    }
}
export default ProductService;
