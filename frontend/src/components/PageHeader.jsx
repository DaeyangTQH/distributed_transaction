import { colors, space, typography } from 'blackchalk'

export default function PageHeader({ title, description, actions }) {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'flex-start',
        justifyContent: 'space-between',
        gap: space[6],
        marginBottom: space[4],
      }}
    >
      <div style={{ flex: 1, minWidth: 0 }}>
        <h1
          style={{
            margin: 0,
            fontFamily: 'inherit',
            fontSize: typography.size['3xl'],
            fontWeight: 700,
            color: colors.fg,
            lineHeight: 1.15,
          }}
        >
          {title}
        </h1>
        {description && (
          <p
            style={{
              margin: `${space[2]} 0 0 0`,
              fontFamily: 'inherit',
              fontSize: typography.size.md,
              color: colors.muted,
              lineHeight: 1.4,
            }}
          >
            {description}
          </p>
        )}
      </div>
      {actions && <div style={{ display: 'flex', gap: space[2], flexShrink: 0 }}>{actions}</div>}
    </div>
  )
}
