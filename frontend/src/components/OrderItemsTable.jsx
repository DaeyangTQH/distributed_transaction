import { SketchTable } from 'blackchalk'
import { formatPrice } from '../utils/format'

const COLUMNS = [
  { key: 'watch', label: 'Watch' },
  { key: 'quantity', label: 'Qty', width: '70px' },
  { key: 'unitPrice', label: 'Unit price' },
  { key: 'lineTotal', label: 'Line total' },
]

export default function OrderItemsTable({ items, watchesById }) {
  const rows = items.map((item) => {
    const watch = watchesById.get(item.watchId)
    const price = item.unitPrice ?? watch?.price
    return {
      watch: watch?.name ?? `Watch #${item.watchId}`,
      quantity: item.quantity,
      unitPrice: price != null ? formatPrice(price) : '—',
      lineTotal: price != null ? formatPrice(Number(price) * item.quantity) : '—',
    }
  })

  return <SketchTable columns={COLUMNS} data={rows} />
}
