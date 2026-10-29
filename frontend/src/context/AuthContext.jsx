import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { login as loginRequest } from '../api/accounts'
import { getToken, setToken, setUnauthorizedHandler } from '../api/client'

export const ADMIN_ROLE = 1

const USER_KEY = 'wms.user'

const AuthContext = createContext(null)

function readStoredUser() {
  if (!getToken()) return null
  try {
    return JSON.parse(localStorage.getItem(USER_KEY))
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)

  const logout = useCallback(() => {
    setToken(null)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  const login = useCallback(async (username, password) => {
    const result = await loginRequest(username, password)
    const nextUser = {
      username: result.username,
      email: result.email,
      role: result.role,
      roleName: result.roleName,
    }
    setToken(result.token)
    localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
    setUser(nextUser)
    return nextUser
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(logout)
    return () => setUnauthorizedHandler(null)
  }, [logout])

  const value = useMemo(
    () => ({ user, isAdmin: user?.role === ADMIN_ROLE, login, logout }),
    [user, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
