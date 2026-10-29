import { SketchBadge } from 'blackchalk'

const TONE_CLASS = {
  CONFIRMED: 'app-success',
  PENDING: 'app-warning',
  CANCELLED: 'app-error',
}

export default function OrderStatusBadge({ status }) {
  return (
    <span className={TONE_CLASS[status]} style={{ display: 'inline-block' }}>
      <SketchBadge>{status}</SketchBadge>
    </span>
  )
}
