import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  SketchButton,
  SketchCard,
  SketchCartItem,
  SketchCartSummary,
  SketchEmptyState,
  SketchList,
  SketchSpinner,
  SketchSplitLayout,
  SketchText,
  space,
} from 'blackchalk'
import { placeOrder } from '../api/orders'
import ErrorAlert from '../components/ErrorAlert'
import PageHeader from '../components/PageHeader'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { formatMoney } from '../utils/format'

export default function CartPage() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const { items, total, setQuantity, removeItem, clear } = useCart()
  const [placing, setPlacing] = useState(false)
  const [error, setError] = useState(null)

  const checkout = async () => {
    if (!user) {
      navigate('/login', { state: { from: '/cart' } })
      return
    }
    setPlacing(true)
    setError(null)
    try {
      const order = await placeOrder(items.map(({ watchId, quantity }) => ({ watchId, quantity })))
      clear()
      navigate(`/orders/${order.orderId}`, { state: { justPlaced: true } })
    } catch (err) {
      setError(err.message)
    } finally {
      setPlacing(false)
    }
  }

  if (items.length === 0) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
        <PageHeader title="Cart" />
        <SketchEmptyState
          icon="shopping-cart"
          title="Your cart is empty"
          description="Add a watch from the catalog to start an order."
          action={
            <SketchButton variant="primary" onClick={() => navigate('/')}>
              Browse watches
            </SketchButton>
          }
        />
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <PageHeader
        title="Cart"
        description="Checkout runs PlaceOrderSaga: create the order, reserve stock for each line, then confirm."
      />
      {error && (
        <ErrorAlert title="Order was not placed" onClose={() => setError(null)}>
          {`${error} Any stock reserved for this attempt was restored and the order was cancelled.`}
        </ErrorAlert>
      )}
      <SketchSplitLayout
        ratio={[2, 1]}
        left={
          <SketchCard block>
            <SketchList>
              {items.map((item) => (
                <SketchCartItem
                  key={item.watchId}
                  name={item.name}
                  price={formatMoney(item.price)}
                  quantity={item.quantity}
                  onQuantityChange={(quantity) => setQuantity(item.watchId, quantity)}
                  onRemove={() => removeItem(item.watchId)}
                />
              ))}
            </SketchList>
          </SketchCard>
        }
        right={
          <div style={{ display: 'flex', flexDirection: 'column', gap: space[3] }}>
            <SketchCartSummary subtotal={formatMoney(total)} shipping="0.00" total={formatMoney(total)} onCheckout={placing ? undefined : checkout} />
            {placing && (
              <div style={{ display: 'flex', alignItems: 'center', gap: space[2] }}>
                <SketchSpinner size={24} />
                <SketchText variant="caption">Running the order saga…</SketchText>
              </div>
            )}
            {!user && <SketchText variant="note">You will be asked to log in before checkout.</SketchText>}
          </div>
        }
      />
    </div>
  )
}
