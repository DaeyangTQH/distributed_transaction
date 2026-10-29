import { SketchAlert } from 'blackchalk'

const TONE_CLASS = {
  error: 'app-error',
  success: 'app-success',
}

export function StatusAlert({ variant, title, children, onClose }) {
  const toneClass = TONE_CLASS[variant]
  const alert = (
    <SketchAlert variant={variant} title={title} onClose={onClose}>
      {children}
    </SketchAlert>
  )
  if (!toneClass) return alert
  return (
    <div className={toneClass} role={variant === 'error' ? 'alert' : 'status'}>
      {alert}
    </div>
  )
}

export function SuccessAlert(props) {
  return <StatusAlert variant="success" {...props} />
}

export default function ErrorAlert(props) {
  return <StatusAlert variant="error" {...props} />
}
