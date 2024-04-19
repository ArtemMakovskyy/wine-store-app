DELETE FROM reviews
WHERE wine_id IN (1, 2)
  AND user_id IN (3, 4);