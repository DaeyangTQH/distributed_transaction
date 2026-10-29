import { SketchOrderStatus } from 'blackchalk'

export default function OrderStatusTrack({ status }) {
  if (status === 'CANCELLED') {
    return <SketchOrderStatus steps={['Pending', 'Cancelled']} currentStep={1} />
  }
  return <SketchOrderStatus steps={['Pending', 'Confirmed']} currentStep={status === 'CONFIRMED' ? 1 : 0} />
}
