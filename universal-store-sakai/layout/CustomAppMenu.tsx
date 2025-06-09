/* eslint-disable @next/next/no-img-element */

import React, { useContext } from 'react';
import AppMenuitem from './AppMenuitem';
import { LayoutContext } from './context/layoutcontext';
import { MenuProvider } from './context/menucontext';
import Link from 'next/link';
import { AppMenuItem } from '@/types';

const CustomAppMenu = (props) => {
    const { layoutConfig } = useContext(LayoutContext);

    /* ORIGINAL */
    /* const model: AppMenuItem[] = [
        {
            label: 'Home',
            items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' }]
        },
        {
            label: 'Admin',
            items: [
                { label: 'User', icon: 'pi pi-fw pi-users', to: '/pages/participant' },
                { label: 'Role', icon: 'pi pi-fw pi-gift', to: '/pages/prize' },
                { label: 'Resources', icon: 'pi pi-fw pi-star', to: '/pages/raffleevent' }
            ]
        }
    ]; */

    const model: AppMenuItem[] = props?.menuList;

    return (
        <MenuProvider>
            <ul className="layout-menu">
                {model.map((item, i) => {
                    return !item?.seperator ? <AppMenuitem item={item} root={true} index={i} key={item.label} /> : <li className="menu-separator"></li>;

                })}
            </ul>
        </MenuProvider>
    );
};

export default CustomAppMenu;
