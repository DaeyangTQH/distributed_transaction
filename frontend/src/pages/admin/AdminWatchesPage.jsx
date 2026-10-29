import { useState } from 'react'
import {
  SketchButton,
  SketchButtonGroup,
  SketchCardGrid,
  SketchInventoryBadge,
  SketchMetricTile,
  SketchModal,
  SketchSearchBar,
  SketchTable,
  SketchText,
  space,
} from 'blackchalk'
import AsyncState from '../../components/AsyncState'
import PageHeader from '../../components/PageHeader'
import { StatusAlert } from '../../components/ErrorAlert'
import WatchFormModal from '../../components/WatchFormModal'
import { deleteWatch } from '../../api/watches'
import { useCatalog } from '../../hooks/useCatalog'
import { formatPrice, stockStatus } from '../../utils/format'

const COLUMNS = [
  { key: 'watchId', label: 'ID', width: '50px' },
  { key: 'name', label: 'Name' },
  { key: 'brand', label: 'Brand' },
  { key: 'category', label: 'Category' },
  { key: 'price', label: 'Price' },
  { key: 'stock', label: 'Stock' },
  { key: 'actions', label: '', width: '180px' },
]

export default function AdminWatchesPage() {
  const { watches, brands, brandsById, categories, loading, error, reload } = useCatalog()
  const [query, setQuery] = useState('')
  const [pendingDelete, setPendingDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const [message, setMessage] = useState(null)
  const [editor, setEditor] = useState(null)

  const handleSaved = (text) => {
    setEditor(null)
    setMessage({ variant: 'success', text })
    reload()
  }

  const needle = query.trim().toLowerCase()
  const visible = watches.filter((watch) => !needle || watch.name.toLowerCase().includes(needle))

  const totalStock = watches.reduce((sum, watch) => sum + watch.stock, 0)
  const outOfStock = watches.filter((watch) => watch.stock <= 0).length

  const confirmDelete = async () => {
    setDeleting(true)
    try {
      await deleteWatch(pendingDelete.watchId)
      setMessage({ variant: 'success', text: `Deleted ${pendingDelete.name}.` })
      reload()
    } catch (err) {
      setMessage({ variant: 'error', text: err.message })
    } finally {
      setDeleting(false)
      setPendingDelete(null)
    }
  }

  const rows = visible.map((watch) => ({
    watchId: watch.watchId,
    name: watch.name,
    brand: brandsById.get(watch.brandId)?.brandName ?? `#${watch.brandId}`,
    category: watch.categoryName,
    price: formatPrice(watch.price),
    stock: (
      <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: space[2] }}>
        <span>{watch.stock}</span>
        <SketchInventoryBadge status={stockStatus(watch.stock)} />
      </div>
    ),
    actions: (
      <SketchButtonGroup gap={space[1]}>
        <SketchButton variant="secondary" onClick={() => setEditor({ watch })}>
          Edit
        </SketchButton>
        <SketchButton variant="tertiary" onClick={() => setPendingDelete(watch)}>
          Delete
        </SketchButton>
      </SketchButtonGroup>
    ),
  }))

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <PageHeader
        title="Manage watches"
        description="Create, update and delete watches. Writes need an administrator token."
        actions={
          <SketchButton variant="primary" onClick={() => setEditor({ watch: null })}>
            New watch
          </SketchButton>
        }
      />
      {message && (
        <StatusAlert variant={message.variant} onClose={() => setMessage(null)}>
          {message.text}
        </StatusAlert>
      )}
      <AsyncState loading={loading} error={error} onRetry={reload}>
        <SketchCardGrid columns={3}>
          <SketchMetricTile label="Watches" value={String(watches.length)} />
          <SketchMetricTile label="Units in stock" value={String(totalStock)} />
          <SketchMetricTile label="Out of stock" value={String(outOfStock)} />
        </SketchCardGrid>
        <SketchSearchBar value={query} onChange={setQuery} placeholder="Filter by name…" />
        <SketchTable columns={COLUMNS} data={rows} />
      </AsyncState>
      <WatchFormModal
        open={editor !== null}
        watch={editor?.watch ?? null}
        brands={brands}
        categories={categories}
        onClose={() => setEditor(null)}
        onSaved={handleSaved}
      />
      <SketchModal open={!!pendingDelete} onClose={() => setPendingDelete(null)} title="Delete watch?">
        <div style={{ display: 'flex', flexDirection: 'column', gap: space[4] }}>
          <SketchText>{`This permanently removes ${pendingDelete?.name ?? ''} from watch-service.`}</SketchText>
          <SketchButtonGroup>
            <SketchButton variant="primary" disabled={deleting} onClick={confirmDelete}>
              {deleting ? 'Deleting…' : 'Delete'}
            </SketchButton>
            <SketchButton variant="secondary" onClick={() => setPendingDelete(null)}>
              Keep it
            </SketchButton>
          </SketchButtonGroup>
        </div>
      </SketchModal>
    </div>
  )
}
