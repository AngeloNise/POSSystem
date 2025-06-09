import { create } from 'zustand';
import { persist } from 'zustand/middleware';

const useAuthStore = create(persist((set ) => ({
    details: null, // Overall data
    userInfo: null, // User only related data
    auth: null,
    login: (userData) => {
      set({ details: userData });
      set({ userInfo: userData.userDetailsLog });
      set({ auth: `Bearer ${userData?.id}` })
    }, // Method to log in
    logout: () => {set({ userInfo: null }); set({ auth: null });}, // Method to log out
    update: (userData) => {set({ userInfo: userData });}
  }),
  {
    name: 'fhrf-auth-storage', // Name of the storage key
    getStorage: () => localStorage, // Use localStorage for persistence
  }));

export default useAuthStore;
