/* eslint-disable @next/next/no-img-element */
'use client';
import { useRouter } from 'next/navigation';
import React, { useContext, useState, useEffect, useRef } from 'react';
import { Checkbox } from 'primereact/checkbox';
import { Button } from 'primereact/button';
import { Password } from 'primereact/password';
import { LayoutContext } from '../../../../layout/context/layoutcontext';
import { InputText } from 'primereact/inputtext';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Toast } from 'primereact/toast';
import { classNames } from 'primereact/utils';
import AuthService from '../../../../service/access_management/AuthService';
import useAuthStore from '@/store/AuthStore';

const LoginPage = () => {
    const toast = useRef(null);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [checked, setChecked] = useState(false);
    const [loadingPage, setLoadingPage] = useState(true);
    const [loadingSignIn, setLoadinggSignIn] = useState(false);
    const { layoutConfig } = useContext(LayoutContext);
    const service = new AuthService();

    const router = useRouter();
    const { login, userInfo } = useAuthStore();
    const containerClassName = classNames('surface-ground flex align-' +
        'items-center justify-content-center min-h-screen min-w-screen overflow-hidden', { 'p-input-filled': layoutConfig.inputStyle === 'filled' });

    useEffect(() => {
        setLoadingPage(false);
    }, [userInfo]);


    const signIn = () => {
        setLoadinggSignIn(true);
        service.signIn(username, password)
            .then(res => {
                setLoadinggSignIn(false);
                console.log(`login | ${JSON.stringify(res)}`);
                if (res.status === 200) {
                    login(res.data);
                    router.push('/')
                } else {
                    toast.current.show({severity:'error', detail: res.status === 401 ? "Invalid credentials"
                        : res.status === 503 ? "Login service not available" : "Login error", life: 3000});
                }
            })
            .catch((error) => {
                setLoadinggSignIn(false);
                toast.current.show({severity:'error', detail: error.status === 401 ? "Invalid credentials" : "Login error", life: 3000});
            })
    }

    return (!loadingPage ?
        <div className={containerClassName}>
            <Toast ref={toast} />
            <div className="flex flex-column align-items-center justify-content-center">
                {/* <img src={`/layout/images/logo-${layoutConfig.colorScheme === 'light' ? 'dark' : 'white'}.svg`} alt="Sakai logo" className="mb-5 w-6rem flex-shrink-0" /> */}
                <div
                    style={{
                        borderRadius: '56px',
                        padding: '0.3rem',
                        background: 'linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)'
                    }}
                >
                    <div className="w-full surface-card py-8 px-5 sm:px-8" style={{ borderRadius: '53px' }}>
                        <div className="text-center mb-5">
                            {/* <img src="/demo/images/login/avatar.png" alt="Image" height="50" className="mb-3" /> */}
                            <div className="text-900 text-3xl font-medium mb-3">Microservice</div>
                            <span className="text-600 font-medium">Sign in to continue</span>
                        </div>

                        <div>
                            <label htmlFor="username1" className="block text-900 text-xl font-medium mb-2">
                                Username
                            </label>
                            <InputText id="username1" type="text" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" className="w-full md:w-30rem mb-5" style={{ padding: '1rem' }} />

                            <label htmlFor="password1" className="block text-900 font-medium text-xl mb-2">
                                Password
                            </label>

                            <Password inputId="password1" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" toggleMask
                                className="w-full mb-5" inputClassName="w-full p-3 md:w-30rem"  feedback={false}></Password>

                            <div className="flex align-items-center justify-content-between mb-5 gap-5">
                                {/* <div className="flex align-items-center">
                                    <Checkbox inputId="rememberme1" checked={checked} onChange={(e) => setChecked(e.checked ?? false)} className="mr-2"></Checkbox>
                                    <label htmlFor="rememberme1">Remember me</label>
                                </div>
                                <a className="font-medium no-underline ml-2 text-right cursor-pointer" style={{ color: 'var(--primary-color)' }}>
                                    Forgot password?
                                </a> */}
                            </div>
                            <Button label="Sign In" className="w-full p-3 text-xl" onClick={() => signIn()} loading={loadingSignIn}></Button>
                            {/* <Button label="Register" className="w-full p-3 text-xl" text onClick={() => router.push('/auth/register')}></Button> */}
                        </div>
                    </div>
                </div>
            </div>
        </div>
        :
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh' // Full viewport height
        }}>
            <ProgressSpinner />
        </div>
    );
};

export default LoginPage;
