import axios from "axios";
import useAuthStore from '@/store/AuthStore';

const useAxiosInstance = () => {
    const { auth } = useAuthStore(); // Access state directly
    return typeof window !== 'undefined' ? axios.create({
        baseURL: window?.location?.protocol.concat("//").concat(window?.location?.hostname).concat(":8090"),
        withCredentials: true,
        headers: { 'Authorization': auth }
    }) : null;
};

export default useAxiosInstance;
