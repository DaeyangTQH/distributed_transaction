const moneyFormatter = new Intl.NumberFormat('en-US', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
})

export const formatMoney = (value) => moneyFormatter.format(Number(value ?? 0))

export const formatPrice = (value) => `$${formatMoney(value)}`

export const formatDateTime = (value) => (value ? new Date(value).toLocaleString() : '—')

export const LOW_STOCK_THRESHOLD = 3

export function stockStatus(stock) {
  if (!stock || stock <= 0) return 'out-of-stock'
  if (stock <= LOW_STOCK_THRESHOLD) return 'low-stock'
  return 'in-stock'
}

/** Order totals from the backend stay 0 until pricing lands in the saga, so estimate from the catalog. */
export function estimateOrderTotal(order, watchesById) {
  return order.items.reduce((sum, item) => {
    const price = item.unitPrice ?? watchesById.get(item.watchId)?.price ?? 0
    return sum + Number(price) * item.quantity
  }, 0)
}
