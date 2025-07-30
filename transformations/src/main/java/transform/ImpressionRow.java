package transform;

import java.util.List;

/** Representation of one row in the impressions dataset. */
public class ImpressionRow {
    public String ranking_id;
    public int customer_id;
    public List<Impression> impression;
    public List<Action> recent_clicks;
    public List<Action> recent_add_carts;
    public String ranking_at;
}
