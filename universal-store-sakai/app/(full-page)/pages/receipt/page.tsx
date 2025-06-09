'use client';
import React, { useState, useRef, useEffect } from 'react';
import useAxiosInstance from '@/util/CustomAxios';
import { useRouter, useSearchParams } from 'next/navigation';
import useAuthStore from '@/store/AuthStore';
import { Toast } from 'primereact/toast';

const ReceiptPage = () => {
    const axiosInstance = useAxiosInstance();
    const { auth } = useAuthStore();
    const router = useRouter();
    const toast = useRef(null);

    const [receiptData, setReceiptData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Store info states
    const [companyName, setCompanyName] = useState('');
    const [storeLogo, setStoreLogo] = useState('');

    const searchParams = useSearchParams();
    const receiptNumber = searchParams.get('receiptNumber');

    const baseReceiptApi = () => "/api/purchase-record/receipt";

    useEffect(() => {
        if (!receiptNumber) {
            setError('Receipt number is required.');
            setLoading(false);
            return;
        }

        setLoading(true);

        if (auth && auth !== 'undefined') {
            const token = auth.startsWith('Bearer ') ? auth : `Bearer ${auth}`;

            axiosInstance.post(baseReceiptApi(), { receiptNumber }, {
                headers: { Authorization: token },
                withCredentials: true,
            })
                .then((response) => {
                    setLoading(false);
                    setReceiptData(Array.isArray(response.data) ? response.data : [response.data]);
                })
                .catch((error) => {
                    setLoading(false);
                    if (error.response?.status === 401) {
                        router.push("/auth/login");
                    } else {
                        setError("Error fetching receipt data");
                        toast.current?.show({ severity: 'error', detail: 'Failed to load receipt', life: 3000 });
                    }
                });

            // Fetch store info
            axiosInstance.post('/api/purchase-record/get', {}, {
                headers: { Authorization: token },
                withCredentials: true,
            })
                .then((res) => {
                    setCompanyName(res.data.storeName);
                    if (res.data.storeLogo) {
                        setStoreLogo('/store-image/' + res.data.storeLogo);
                    } else {
                        setStoreLogo('');
                    }
                })
                .catch((err) => {
                    console.error("Failed to fetch store info", err);
                });
        } else {
            setLoading(false);
        }
    }, [receiptNumber, auth]);

    const total = receiptData.reduce((sum, item) => sum + item.totalPrice, 0);

    return (
        <div className="grid font-sans text-sm px-4 py-8 bg-white shadow-md" style={{ maxWidth: '25%', margin: '3% auto' }}>
            <Toast ref={toast} />
            <div className="col-12 text-center -mt-6">
                {storeLogo && (
                    <img
                        src={storeLogo}
                        alt="Store Logo"
                        className="mb-2"
                        style={{ height: '60px', objectFit: 'contain' }}
                        onError={e => e.currentTarget.style.display = 'none'} // optional: hide if image fails to load
                    />
                )}
                <h1 className="text-lg font-bold">{companyName || ""}</h1>
            </div>


            {loading && <p>Loading...</p>}
            {error && <p className="text-red-500">{error}</p>}

            {!loading && receiptData.length > 0 && (
                <>
                    <div className="col-12 xl:col-8">
                        <div className="space-y-2 text-sm">
                            <p className="text-gray-800 font-bold">
                                Counter: <span className="text-gray-800 font-normal">{receiptData[0].counter}</span>
                            </p>
                            <p className="text-gray-800 font-bold">
                                Issued By: <span className="text-gray-800 font-normal">{receiptData[0].createdBy}</span>
                            </p>
                            <p className="text-gray-800 font-bold">
                                Receipt Number: <span className="text-gray-800 font-normal">{receiptData[0].receiptNumber}</span>
                            </p>
                            <p className="text-gray-800 font-bold">
                                Issued Date: <span className="text-gray-800 font-normal">
                                    {new Date(receiptData[0].createdDate).toLocaleString('en-CA', {
                                        year: 'numeric',
                                        month: '2-digit',
                                        day: '2-digit',
                                        hour: '2-digit',
                                        minute: '2-digit',
                                        second: '2-digit',
                                        hour12: false
                                    }).replace(',', '')}
                                </span>
                            </p>
                        </div>
                    </div>

                    <div className="col-12 mt-6 text-xs font-mono">
                        <table className="w-full text-left border-collapse">
                            {/* Simulated header row */}
                            <tbody>
                            <tr className="font-bold">
                                <td className="pr-2">Product Name</td>
                                <td className="pr-2">Serial Number</td>
                                <td className="pr-2 text-right">Price</td>
                                <td className="pr-2 text-right">Qty</td>
                                <td className="text-right">Total</td>
                            </tr>

                            {/* Item rows */}
                            {receiptData.map((item, idx) => (
                                <tr key={idx}>
                                    <td className="pr-2">{item.productName}</td>
                                    <td className="pr-2">{item.serialNumber || '-'}</td>
                                    <td className="pr-2 text-right">₱{item.price.toFixed(2)}</td>
                                    <td className="pr-2 text-right">{item.quantity}</td>
                                    <td className="text-right">₱{item.totalPrice.toFixed(2)}</td>
                                </tr>
                            ))}

                            {/* Total row */}
                            <tr className="font-bold border-t border-gray-300">
                                <td colSpan="4" className="pt-2 text-right">Total:</td>
                                <td className="pt-2 text-right">₱{total.toFixed(2)}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </>
            )}
        </div>
    );
};

export default ReceiptPage;
