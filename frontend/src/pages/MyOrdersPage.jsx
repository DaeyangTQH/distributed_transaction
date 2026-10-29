import { useNavigate } from 'react-router-dom'
import { SketchButton, SketchEmptyState, SketchTable, space } from 'blackchalk'
import AsyncState from '../components/AsyncState'
import PageHeader from '../components/PageHeader'
import OrderStatusBadge from '../components/OrderStatusBadge'
import { listMyOrders } from '../api/orders'
import { useAsync } from '../hooks/useAsync'
import { useCatalog } from '../hooks/useCatalog'
import { estimateOrderTotal, formatDateTime, formatPrice } from '../utils/format'

const COLUMNS = [
  { key: 'orderId', label: 'Order', width: '90px' },
  { key: 'createdAt', label: 'Placed' },
  { key: 'items', label: 'Items', width: '80px' },
  { key: 'total', label: 'Est. total' },
  { key: 'status', label: 'Status', width: '140px' },
]

export default function MyOrdersPage() {
  const navigate = useNavigate()
  const orders = useAsync(listMyOrders, [])
  const { watchesById } = useCatalog()

  const rows = (orders.data ?? []).map((order) => ({
    id: order.orderId,
    orderId: `#${order.orderId}`,
    createdAt: formatDateTime(order.createdAt),
    items: order.items.reduce((sum, item) => sum + item.quantity, 0),
    total: formatPrice(estimateOrderTotal(order, watchesById)),
    status: <OrderStatusBadge status={order.status} />,
  }))

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <PageHeader
        title="My orders"
        description="Orders placed with your account. Pick a row to see its items and saga outcome."
        actions={
          <SketchButton variant="secondary" onClick={orders.reload}>
            Refresh
          </SketchButton>
        }
      />
      <AsyncState loading={orders.loading} error={orders.error} onRetry={orders.reload}>
        {rows.length === 0 ? (
          <SketchEmptyState
            icon="receipt"
            title="No orders yet"
            action={
              <SketchButton variant="primary" onClick={() => navigate('/')}>
                Browse watches
              </SketchButton>
            }
          />
        ) : (
          <SketchTable columns={COLUMNS} data={rows} onRowClick={(row) => navigate(`/orders/${row.id}`)} />
        )}
      </AsyncState>
    </div>
  )
}
