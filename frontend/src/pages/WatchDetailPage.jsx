import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  SketchAlert,
  SketchBreadcrumb,
  SketchButton,
  SketchButtonGroup,
  SketchFormRow,
  SketchInventoryBadge,
  SketchPanel,
  SketchPriceTag,
  SketchSplitLayout,
  SketchText,
  space,
} from 'blackchalk'
import AsyncState from '../components/AsyncState'
import WatchImage from '../components/WatchImage'
import { SuccessAlert } from '../components/ErrorAlert'
import QuantityStepper from '../components/QuantityStepper'
import { getBrand } from '../api/brands'
import { getWatch } from '../api/watches'
import { useCart } from '../context/CartContext'
import { useAsync } from '../hooks/useAsync'
import { formatMoney, stockStatus } from '../utils/format'

export default function WatchDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { addItem, items } = useCart()
  const [quantity, setQuantity] = useState(1)
  const [added, setAdded] = useState(false)

  const { data, loading, error, reload } = useAsync(async () => {
    const watch = await getWatch(id)
    const brand = await getBrand(watch.brandId).catch(() => null)
    return { watch, brand }
  }, [id])

  const watch = data?.watch
  const inCart = items.find((item) => item.watchId === watch?.watchId)?.quantity ?? 0
  const available = watch ? Math.max(watch.stock - inCart, 0) : 0

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: space[5] }}>
      <SketchBreadcrumb
        items={[{ label: 'Catalog', onClick: () => navigate('/') }, { label: watch?.name ?? 'Watch' }]}
      />
      <AsyncState loading={loading} error={error} onRetry={reload}>
        {watch && (
          <SketchSplitLayout
            ratio={[1, 1]}
            left={<WatchImage watch={watch} height={420} />}
            right={
              <div style={{ display: 'flex', flexDirection: 'column', gap: space[4] }}>
                <SketchText variant="title" as="h1">
                  {watch.name}
                </SketchText>
                <SketchText variant="lead">
                  {`${data.brand?.brandName ?? 'Unknown brand'}${data.brand?.countryOfOrigin ? `, ${data.brand.countryOfOrigin}` : ''}`}
                </SketchText>
                <div style={{ display: 'flex', alignItems: 'center', gap: space[3] }}>
                  <SketchPriceTag price={formatMoney(watch.price)} currency="$" />
                  <SketchInventoryBadge status={stockStatus(watch.stock)} />
                  <SketchText variant="caption">{`${watch.stock} in stock`}</SketchText>
                </div>
                <SketchPanel title="Specifications">
                  <SketchFormRow label="Category">
                    <SketchText>{watch.categoryName}</SketchText>
                  </SketchFormRow>
                  <SketchFormRow label="Case">
                    <SketchText>{watch.caseMaterial}</SketchText>
                  </SketchFormRow>
                  <SketchFormRow label="Strap">
                    <SketchText>{watch.strapMaterial}</SketchText>
                  </SketchFormRow>
                  <SketchFormRow label="Water resistance">
                    <SketchText>{watch.waterResistance}</SketchText>
                  </SketchFormRow>
                  <SketchFormRow label="Released">
                    <SketchText>{watch.releaseDate ?? '—'}</SketchText>
                  </SketchFormRow>
                </SketchPanel>
                {added && (
                  <SuccessAlert onClose={() => setAdded(false)}>Added to cart.</SuccessAlert>
                )}
                {available > 0 ? (
                  <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: space[3] }}>
                    <QuantityStepper value={Math.min(quantity, available)} min={1} max={available} onChange={setQuantity} />
                    <SketchButtonGroup maxWidth="auto">
                      <SketchButton
                        variant="primary"
                        onClick={() => {
                          addItem(watch, Math.min(quantity, available))
                          setQuantity(1)
                          setAdded(true)
                        }}
                      >
                        Add to cart
                      </SketchButton>
                      <SketchButton variant="secondary" onClick={() => navigate('/cart')}>
                        Go to cart
                      </SketchButton>
                    </SketchButtonGroup>
                  </div>
                ) : (
                  <SketchAlert variant="warning">
                    {watch.stock > 0 ? 'All available stock is already in your cart.' : 'This watch is out of stock.'}
                  </SketchAlert>
                )}
              </div>
            }
          />
        )}
      </AsyncState>
    </div>
  )
}
