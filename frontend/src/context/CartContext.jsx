import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'

const CART_KEY = 'wms.cart'

const CartContext = createContext(null)

function readStoredCart() {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY)) ?? []
  } catch {
    return []
  }
}

export function CartProvider({ children }) {
  const [items, setItems] = useState(readStoredCart)

  useEffect(() => {
    localStorage.setItem(CART_KEY, JSON.stringify(items))
  }, [items])

  const addItem = useCallback((watch, quantity) => {
    setItems((current) => {
      const existing = current.find((item) => item.watchId === watch.watchId)
      if (existing) {
        return current.map((item) =>
          item.watchId === watch.watchId
            ? { ...item, quantity: Math.min(item.quantity + quantity, watch.stock) }
            : item,
        )
      }
      return [
        ...current,
        {
          watchId: watch.watchId,
          name: watch.name,
          price: Number(watch.price),
          stock: watch.stock,
          quantity: Math.min(quantity, watch.stock),
        },
      ]
    })
  }, [])

  const setQuantity = useCallback((watchId, quantity) => {
    setItems((current) =>
      quantity <= 0
        ? current.filter((item) => item.watchId !== watchId)
        : current.map((item) =>
            item.watchId === watchId ? { ...item, quantity: Math.min(quantity, item.stock) } : item,
          ),
    )
  }, [])

  const removeItem = useCallback((watchId) => {
    setItems((current) => current.filter((item) => item.watchId !== watchId))
  }, [])

  const clear = useCallback(() => setItems([]), [])

  const value = useMemo(() => {
    const count = items.reduce((sum, item) => sum + item.quantity, 0)
    const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0)
    return { items, count, total, addItem, setQuantity, removeItem, clear }
  }, [items, addItem, setQuantity, removeItem, clear])

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

export function useCart() {
  return useContext(CartContext)
}
