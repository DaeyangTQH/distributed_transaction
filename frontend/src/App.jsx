import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import { AuthProvider } from './context/AuthContext'
import { CartProvider } from './context/CartContext'
import CartPage from './pages/CartPage'
import CatalogPage from './pages/CatalogPage'
import LoginPage from './pages/LoginPage'
import MyOrdersPage from './pages/MyOrdersPage'
import OrderDetailPage from './pages/OrderDetailPage'
import WatchDetailPage from './pages/WatchDetailPage'
import AdminOrdersPage from './pages/admin/AdminOrdersPage'
import AdminWatchesPage from './pages/admin/AdminWatchesPage'
export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<Layout />}>
              <Route index element={<CatalogPage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="watches/:id" element={<WatchDetailPage />} />
              <Route path="cart" element={<CartPage />} />
              <Route element={<ProtectedRoute />}>
                <Route path="orders" element={<MyOrdersPage />} />
                <Route path="orders/:id" element={<OrderDetailPage />} />
              </Route>
              <Route path="admin" element={<ProtectedRoute adminOnly />}>
                <Route path="watches" element={<AdminWatchesPage />} />
                <Route path="orders" element={<AdminOrdersPage />} />
              </Route>
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </CartProvider>
    </AuthProvider>
  )
}
