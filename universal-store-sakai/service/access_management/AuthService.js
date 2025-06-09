//import axios from "axios";
import createAxiosInstance from "../../util/CustomAxios";

export class AuthService {

    constructor() {
        this.axiosInstance = createAxiosInstance();
    }

    baseApi() {
        return "/auth";
    }

    signIn(username, password) {
        return this.axiosInstance.post(this.baseApi() + "/login", {
            "username": username,
            "password": password
        })
    }

    signUp(account) {
        return this.axiosInstance.post(this.baseApi() + "/signup", account)
    }

    signOut() {
        return this.axiosInstance.post(this.baseApi() + "/logout")
    }

}
export default AuthService;