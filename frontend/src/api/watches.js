import { request } from './client'

export const listWatches = () => request('/watches', { auth: false })

export const getWatch = (id) => request(`/watches/${id}`, { auth: false })

export const createWatch = (watch) => request('/watches', { method: 'POST', body: watch })

export const updateWatch = (id, watch) => request(`/watches/${id}`, { method: 'PUT', body: watch })

export const deleteWatch = (id) => request(`/watches/${id}`, { method: 'DELETE' })
