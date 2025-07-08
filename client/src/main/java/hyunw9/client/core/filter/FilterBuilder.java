package hyunw9.client.core.filter;

import java.util.List;

import hyunw9.client.core.RequestFilter;
import hyunw9.client.transport.HttpTransport;

public final class FilterBuilder {
	public static RequestFilter build(List<RequestFilter> filters, HttpTransport tx) {

		RequestFilter tail = (spec, n) -> tx.send(spec); //맨 끝에서 실제 전송을 담당한다.

		for (int i = filters.size()-1; i >= 0; i--) {
			RequestFilter f = filters.get(i);
			RequestFilter next = tail;
			tail = (spec, n) -> f.apply(spec, s -> next.apply(s, n)); // 합성
		}
		return tail;
	}
}
