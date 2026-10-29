import { useLocation, useNavigate, useParams } from 'react-router-dom'
import {
  SketchAlert,
  SketchBreadcrumb,
  SketchCardGrid,
  SketchMetricTile,
  SketchSection,
  SketchText,
  space,
} from 'blackchalk'
import AsyncState from '../components/AsyncState'
import PageHeader from '../components/PageHeader'
import { SuccessAlert } from '../components/ErrorAlert'
import OrderItemsTable from '../components/OrderItemsTable'
import OrderStatusTrack from '../components/OrderStatusTrack'
import { getOrder } from '../api/orders'
import { useAsync } from '../hooks/useAsync'
import { useCatalog } from '../hooks/useCatalog'
import { estimateOrderTotal, formatDateTime, formatPrice } from '../utils/format'

export default function OrderDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const { data: order, loading, error, reload } = useAsync(() => getOrder(id), [id])
  const { watchesById } = useCatalog()

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <SketchBreadcrumb items={[{ label: 'My orders', onClick: () => navigate('/orders') }, { label: `Order #${id}` }]} />
      <AsyncState loading={loading} error={error} onRetry={reload}>
        {order && (
          <>
            <PageHeader title={`Order #${order.orderId}`} description={`Placed ${formatDateTime(order.createdAt)}`} />
            {location.state?.justPlaced && order.status === 'CONFIRMED' && (
              <SuccessAlert title="Order confirmed">
                The saga reserved stock for every line and confirmed the order.
              </SuccessAlert>
            )}
            {order.status === 'CANCELLED' && (
              <SketchAlert variant="warning" title="Order cancelled">
                Reserved stock for this order was restored.
              </SketchAlert>
            )}
            <OrderStatusTrack status={order.status} />
            <SketchCardGrid columns={3}>
              <SketchMetricTile label="Status" value={order.status} />
              <SketchMetricTile label="Items" value={String(order.items.reduce((sum, item) => sum + item.quantity, 0))} />
              <SketchMetricTile label="Estimated total" value={formatPrice(estimateOrderTotal(order, watchesById))} />
            </SketchCardGrid>
            <SketchSection title="Items">
              <OrderItemsTable items={order.items} watchesById={watchesById} />
              <SketchText variant="note">
                Prices come from the current catalog, because order-service does not store unit prices yet.
              </SketchText>
            </SketchSection>
          </>
        )}
      </AsyncState>
    </div>
  )
}
