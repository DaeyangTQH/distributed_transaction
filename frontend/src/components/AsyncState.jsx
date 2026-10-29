import { SketchButton, SketchSpinner, space } from 'blackchalk'
import ErrorAlert from './ErrorAlert'

export default function AsyncState({ loading, error, onRetry, children }) {
  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: space[10] }}>
        <SketchSpinner size={48} />
      </div>
    )
  }

  if (error) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', gap: space[3] }}>
        <ErrorAlert title="Something went wrong">{error.message}</ErrorAlert>
        {onRetry && (
          <SketchButton variant="secondary" onClick={onRetry}>
            Try again
          </SketchButton>
        )}
      </div>
    )
  }

  return children
}
