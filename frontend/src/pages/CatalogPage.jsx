import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  SketchButton,
  SketchCard,
  SketchCheckbox,
  SketchEmptyState,
  SketchInventoryBadge,
  SketchPriceTag,
  SketchProductGrid,
  SketchRadioGroup,
  SketchSearchBar,
  SketchText,
  space,
} from 'blackchalk'
import AsyncState from '../components/AsyncState'
import PageHeader from '../components/PageHeader'
import WatchImage from '../components/WatchImage'
import { useCatalog } from '../hooks/useCatalog'
import { formatMoney, stockStatus } from '../utils/format'

const SORT_OPTIONS = [
  { value: 'name', label: 'Name' },
  { value: 'price-asc', label: 'Price: low to high' },
  { value: 'price-desc', label: 'Price: high to low' },
  { value: 'stock', label: 'Most in stock' },
]

const SORTERS = {
  name: (a, b) => a.name.localeCompare(b.name),
  'price-asc': (a, b) => a.price - b.price,
  'price-desc': (a, b) => b.price - a.price,
  stock: (a, b) => b.stock - a.stock,
}

function FilterGroup({ label, value, onChange, options }) {
  const keyOf = (optionValue) => (optionValue === null ? 'all' : String(optionValue))
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[2] }}>
      <SketchText variant="label">{label}</SketchText>
      <SketchRadioGroup
        name={label}
        orientation="vertical"
        value={keyOf(value)}
        onChange={(key) => onChange(options.find((option) => keyOf(option.value) === key).value)}
        options={options.map((option) => ({ value: keyOf(option.value), label: option.label }))}
      />
    </div>
  )
}

const VISIBLE_OPTIONS = 3

function CheckboxFilter({ label, options, selected, onChange }) {
  const [expanded, setExpanded] = useState(false)
  const shown = expanded ? options : options.slice(0, VISIBLE_OPTIONS)
  const hidden = options.length - VISIBLE_OPTIONS

  const toggle = (value, checked) =>
    onChange(checked ? [...selected, value] : selected.filter((item) => item !== value))

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[2] }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <SketchText variant="label">{label}</SketchText>
        {selected.length > 0 && (
          <SketchButton variant="tertiary" onClick={() => onChange([])}>
            {`Clear (${selected.length})`}
          </SketchButton>
        )}
      </div>
      {shown.map((option) => (
        <div key={option.value} style={{ display: 'flex', alignItems: 'center', gap: space[2] }}>
          <SketchCheckbox
            aria-label={option.label}
            checked={selected.includes(option.value)}
            onChange={(checked) => toggle(option.value, checked)}
          />
          <span
            aria-hidden="true"
            onClick={() => toggle(option.value, !selected.includes(option.value))}
            style={{ cursor: 'pointer' }}
          >
            <SketchText>{option.label}</SketchText>
          </span>
        </div>
      ))}
      {hidden > 0 && (
        <div>
          <SketchButton variant="tertiary" onClick={() => setExpanded((open) => !open)}>
            {expanded ? 'Show less' : `Show more (${hidden})`}
          </SketchButton>
        </div>
      )}
    </div>
  )
}

export default function CatalogPage() {
  const navigate = useNavigate()
  const { watches, brands, brandsById, categories, loading, error, reload } = useCatalog()
  const [brandIds, setBrandIds] = useState([])
  const [categoryIds, setCategoryIds] = useState([])
  const [query, setQuery] = useState('')
  const [sort, setSort] = useState('name')

  const visible = useMemo(() => {
    const needle = query.trim().toLowerCase()
    return watches
      .filter((watch) => brandIds.length === 0 || brandIds.includes(watch.brandId))
      .filter((watch) => categoryIds.length === 0 || categoryIds.includes(watch.categoryId))
      .filter((watch) => !needle || watch.name.toLowerCase().includes(needle))
      .sort(SORTERS[sort])
  }, [watches, brandIds, categoryIds, query, sort])

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <PageHeader
        title="Watch catalog"
        description="Browse the collection from watch-service, filtered by brand-service brands."
      />
      <AsyncState loading={loading} error={error} onRetry={reload}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: space[6] }}>
          <aside style={{ flex: '0 0 240px', display: 'flex', flexDirection: 'column', gap: space[5] }}>
            <CheckboxFilter
              label="Brand"
              options={brands.map((brand) => ({ value: brand.brandId, label: brand.brandName }))}
              selected={brandIds}
              onChange={setBrandIds}
            />
            <CheckboxFilter
              label="Category"
              options={categories.map((category) => ({ value: category.categoryId, label: category.categoryName }))}
              selected={categoryIds}
              onChange={setCategoryIds}
            />
            <FilterGroup label="Sort by" value={sort} onChange={setSort} options={SORT_OPTIONS} />
          </aside>
          <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', gap: space[4] }}>
            <SketchSearchBar value={query} onChange={setQuery} placeholder="Search watches…" />
            <SketchText variant="caption">{`${visible.length} of ${watches.length} watches`}</SketchText>
            {visible.length === 0 ? (
              <SketchEmptyState
                icon="search"
                title="No watches match"
                description="Clear a filter or change the search to see more."
              />
            ) : (
              <SketchProductGrid columns={4} minColWidth={240}>
                {visible.map((watch) => (
                  <SketchCard
                    key={watch.watchId}
                    block
                    role="link"
                    tabIndex={0}
                    aria-label={`Open ${watch.name}`}
                    onClick={() => navigate(`/watches/${watch.watchId}`)}
                    onKeyDown={(event) => {
                      if (event.key === 'Enter') navigate(`/watches/${watch.watchId}`)
                    }}
                  >
                    <div style={{ display: 'flex', flexDirection: 'column', gap: space[2] }}>
                      <WatchImage watch={watch} height={200} />
                      <SketchText variant="heading" as="h3">
                        {watch.name}
                      </SketchText>
                      <SketchText variant="caption">
                        {`${brandsById.get(watch.brandId)?.brandName ?? 'Unknown brand'} · ${watch.categoryName}`}
                      </SketchText>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <SketchPriceTag price={formatMoney(watch.price)} currency="$" />
                        <SketchInventoryBadge status={stockStatus(watch.stock)} />
                      </div>
                    </div>
                  </SketchCard>
                ))}
              </SketchProductGrid>
            )}
          </div>
        </div>
      </AsyncState>
    </div>
  )
}
