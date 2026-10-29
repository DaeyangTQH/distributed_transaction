import { request } from './client'

export const placeOrder = (items) => request('/orders', { method: 'POST', body: { items } })

export const listMyOrders = () => request('/orders/me')

export const getOrder = (id) => request(`/orders/${id}`)

export const listAllOrders = () => request('/orders')

export const confirmOrder = (id) => request(`/orders/${id}/confirm`, { method: 'PATCH' })

export const cancelOrder = (id) => request(`/orders/${id}/cancel`, { method: 'PATCH' })
