import { request } from './client'

export const listBrands = () => request('/brands', { auth: false })

export const getBrand = (id) => request(`/brands/${id}`, { auth: false })
