import { SketchButton, SketchIcon, SketchText, space } from 'blackchalk'

export default function QuantityStepper({ value, min = 1, max, onChange }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: space[2] }}>
      <SketchButton
        variant="secondary"
        aria-label="Decrease quantity"
        disabled={value <= min}
        onClick={() => onChange(Math.max(value - 1, min))}
      >
        <SketchIcon name="minus" size={16} />
      </SketchButton>
      <SketchText variant="heading" as="span" aria-live="polite" style={{ minWidth: space[8], textAlign: 'center' }}>
        {value}
      </SketchText>
      <SketchButton
        variant="secondary"
        aria-label="Increase quantity"
        disabled={max !== undefined && value >= max}
        onClick={() => onChange(max !== undefined ? Math.min(value + 1, max) : value + 1)}
      >
        <SketchIcon name="plus" size={16} />
      </SketchButton>
    </div>
  )
}
