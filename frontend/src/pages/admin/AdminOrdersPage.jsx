import { useState } from 'react'
import {
  SketchButton,
  SketchButtonGroup,
  SketchCardGrid,
  SketchFilterBar,
  SketchMetricTile,
  SketchModal,
  SketchTable,
  SketchText,
  space,
} from 'blackchalk'
import AsyncState from '../../components/AsyncState'
import PageHeader from '../../components/PageHeader'
import { StatusAlert } from '../../components/ErrorAlert'
import OrderItemsTable from '../../components/OrderItemsTable'
import OrderStatusBadge from '../../components/OrderStatusBadge'
import OrderStatusTrack from '../../components/OrderStatusTrack'
import { cancelOrder, confirmOrder, listAllOrders } from '../../api/orders'
import { useAsync } from '../../hooks/useAsync'
import { useCatalog } from '../../hooks/useCatalog'
import { estimateOrderTotal, formatDateTime, formatPrice } from '../../utils/format'

const STATUSES = ['ALL', 'PENDING', 'CONFIRMED', 'CANCELLED']

const COLUMNS = [
  { key: 'orderId', label: 'Order', width: '70px' },
  { key: 'accountId', label: 'Account', width: '80px' },
  { key: 'createdAt', label: 'Placed' },
  { key: 'total', label: 'Est. total' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: '', width: '280px' },
]

export default function AdminOrdersPage() {
  const orders = useAsync(listAllOrders, [])
  const { watchesById } = useCatalog()
  const [filter, setFilter] = useState('ALL')
  const [selected, setSelected] = useState(null)
  const [busyId, setBusyId] = useState(null)
  const [message, setMessage] = useState(null)

  const all = orders.data ?? []
  const visible = filter === 'ALL' ? all : all.filter((order) => order.status === filter)
  const countOf = (status) => all.filter((order) => order.status === status).length

  const runAction = async (order, action, label) => {
    setBusyId(order.orderId)
    try {
      await action(order.orderId)
      setMessage({ variant: 'success', text: `Order #${order.orderId} ${label}.` })
      orders.reload()
    } catch (err) {
      setMessage({ variant: 'error', text: err.message })
    } finally {
      setBusyId(null)
    }
  }

  const rows = visible.map((order) => ({
    orderId: `#${order.orderId}`,
    accountId: order.accountId,
    createdAt: formatDateTime(order.createdAt),
    total: formatPrice(estimateOrderTotal(order, watchesById)),
    status: <OrderStatusBadge status={order.status} />,
    actions: (
      <SketchButtonGroup gap={space[1]}>
        <SketchButton variant="tertiary" onClick={() => setSelected(order)}>
          Details
        </SketchButton>
        {order.status === 'PENDING' && (
          <SketchButton
            variant="secondary"
            disabled={busyId === order.orderId}
            onClick={() => runAction(order, confirmOrder, 'confirmed')}
          >
            Confirm
          </SketchButton>
        )}
        {order.status !== 'CANCELLED' && (
          <SketchButton
            variant="secondary"
            disabled={busyId === order.orderId}
            onClick={() => runAction(order, cancelOrder, 'cancelled')}
          >
            Cancel
          </SketchButton>
        )}
      </SketchButtonGroup>
    ),
  }))

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <PageHeader
        title="All orders"
        description="Every order in order-service, newest first."
        actions={
          <SketchButton variant="secondary" onClick={orders.reload}>
            Refresh
          </SketchButton>
        }
      />
      {message && (
        <StatusAlert variant={message.variant} onClose={() => setMessage(null)}>
          {message.text}
        </StatusAlert>
      )}
      <AsyncState loading={orders.loading} error={orders.error} onRetry={orders.reload}>
        <SketchCardGrid columns={3}>
          <SketchMetricTile label="Pending" value={String(countOf('PENDING'))} />
          <SketchMetricTile label="Confirmed" value={String(countOf('CONFIRMED'))} />
          <SketchMetricTile label="Cancelled" value={String(countOf('CANCELLED'))} />
        </SketchCardGrid>
        <SketchFilterBar
          filters={STATUSES.map((status) => ({
            label: status === 'ALL' ? 'All' : status,
            active: filter === status,
            onClick: () => setFilter(status),
          }))}
        />
        <SketchText variant="note">
          Cancel here only changes the order status; it does not run saga compensation, so reserved stock is not restored.
        </SketchText>
        <SketchTable columns={COLUMNS} data={rows} />
      </AsyncState>
      <SketchModal
        open={!!selected}
        onClose={() => setSelected(null)}
        title={selected ? `Order #${selected.orderId}` : undefined}
      >
        {selected && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: space[4], minWidth: 'min(640px, 80vw)' }}>
            <SketchText variant="caption">
              {`Account ${selected.accountId} · placed ${formatDateTime(selected.createdAt)} · updated ${formatDateTime(selected.updatedAt)}`}
            </SketchText>
            <OrderStatusTrack status={selected.status} />
            <OrderItemsTable items={selected.items} watchesById={watchesById} />
            <SketchText variant="heading">{`Estimated total ${formatPrice(estimateOrderTotal(selected, watchesById))}`}</SketchText>
          </div>
        )}
      </SketchModal>
    </div>
  )
}
