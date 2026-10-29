import { request } from './client'

export const login = (username, password) =>
  request('/accounts/login', { method: 'POST', body: { username, password }, auth: false })
