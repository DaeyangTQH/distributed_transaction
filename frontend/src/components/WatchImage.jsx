import { SketchImagePlaceholder } from 'blackchalk'

const CATEGORY_IMAGES = [
  ['dive', '/watches/dive.jpg'],
  ['chrono', '/watches/chronograph.jpg'],
  ['dress', '/watches/dress.jpg'],
  ['digital', '/watches/digital.jpg'],
  ['pilot', '/watches/pilot.jpg'],
]

function watchImageFor(watch) {
  const category = watch?.categoryName?.toLowerCase() ?? ''
  return CATEGORY_IMAGES.find(([keyword]) => category.includes(keyword))?.[1] ?? null
}

export default function WatchImage({ watch, height }) {
  const src = watchImageFor(watch)
  if (!src) return <SketchImagePlaceholder height={height} label={watch?.name} />

  return (
    <img
      src={src}
      alt={watch.name}
      loading="lazy"
      style={{
        display: 'block',
        width: '100%',
        height,
        objectFit: 'cover',
        border: '2px solid var(--sketch-fg)',
        borderRadius: 6,
        background: 'var(--sketch-fill-faint)',
      }}
    />
  )
}
