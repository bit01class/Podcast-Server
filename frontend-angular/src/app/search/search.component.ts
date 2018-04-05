import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PageEvent } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { debounceTime, map } from 'rxjs/operators';

import { OpenSideNavAction } from '../app.actions';
import { AppState } from '../app.reducer';
import { ComponentDestroyCompanion } from '../shared/component.utils';
import { Direction, Item, Page, SearchItemPageRequest, Sort, Status } from '../shared/entity';
import { defaultSearch } from '../shared/service/item/item.service';

import { Search } from './search.actions';
import { selectResults } from './search.reducer';

interface SearchItemRequestViewModel {
	q?: string;
	page?: number;
	size?: number;
	status?: StatusesViewValue;
	tags?: string;
	sort?: Sort;
}

export enum StatusesViewValue {
	ALL,
	DOWNLOADED,
	NOT_DOWNLOADED
}

@Component({
	selector: 'ps-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, OnDestroy {
	doNotEmit = { emitEvent: false };

	statuses = [
		{ title: 'All', value: StatusesViewValue.ALL },
		{ title: 'Downloaded', value: StatusesViewValue.DOWNLOADED },
		{ title: 'Not Downloaded', value: StatusesViewValue.NOT_DOWNLOADED }
	];

	properties = [
		{ title: 'Relevance', value: 'pertinence' },
		{ title: 'Publication Date', value: 'pubDate' },
		{ title: 'Download Date', value: 'downloadDate' }
	];

	directions = [{ title: 'Descendant', value: Direction.DESC }, { title: 'Ascendant', value: Direction.ASC }];

	form: FormGroup;
	items: Page<Item>;

	companion = new ComponentDestroyCompanion();

	constructor(private route: ActivatedRoute, private store: Store<AppState>, private formBuilder: FormBuilder) {}

	ngOnInit() {
		const untilDestroy = this.companion.untilDestroy();

		this.form = this.formBuilder.group({
			q: [''],
			tags: [''],
			page: [],
			size: [],
			status: [StatusesViewValue.ALL],
			sort: this.formBuilder.group({ direction: [Direction.DESC], property: ['pubDate'] })
		});

		this.form.valueChanges.pipe(untilDestroy(), debounceTime(500), map(toSearchItemRequest)).subscribe(v => this.search(v));

		this.store.pipe(select(selectResults), untilDestroy()).subscribe(s => (this.items = s));

		this.route.data.pipe(untilDestroy(), map(d => d.search)).subscribe(s => (this.items = s));

		this.route.data.pipe(untilDestroy(), map(d => d.request)).subscribe(r => {
			this.form.get('q').setValue(r.q, this.doNotEmit);
			this.form.get('tags').setValue(r.tags.map(t => t.name).join(', '), this.doNotEmit);
			this.form.get('status').setValue(toStatusesValue(r.status), this.doNotEmit);
			this.form.get('size').setValue(r.size, this.doNotEmit);
			this.form.get('page').setValue(r.page, this.doNotEmit);
			this.form
				.get('sort')
				.get('direction')
				.setValue(r.sort[0].direction, this.doNotEmit);
			this.form
				.get('sort')
				.get('property')
				.setValue(r.sort[0].property, this.doNotEmit);
		});
	}

	search(v: SearchItemPageRequest): void {
		this.store.dispatch(new Search({ ...defaultSearch, ...v }));
	}

	changePage(e: PageEvent) {
		this.form.get('size').setValue(e.pageSize, this.doNotEmit);
		this.form.get('page').setValue(e.pageIndex, this.doNotEmit);
		this.form.updateValueAndValidity({ onlySelf: false, emitEvent: true });
	}

	openSideNav() {
		this.store.dispatch(new OpenSideNavAction());
	}

	ngOnDestroy(): void {
		this.companion.destroy();
	}
}

function toStatus(v: StatusesViewValue): Status[] {
	switch (v) {
		case StatusesViewValue.ALL:
			return [];
		case StatusesViewValue.DOWNLOADED:
			return [Status.FINISH];
		default:
			return [Status.NOT_DOWNLOADED, Status.DELETED, Status.STARTED, Status.STOPPED, Status.PAUSED, Status.FAILED];
	}
}

function toStatusesValue(v: Status[]): StatusesViewValue {
	if (v.includes(Status.FINISH)) {
		return StatusesViewValue.DOWNLOADED;
	}

	if (v.includes(Status.NOT_DOWNLOADED)) {
		return StatusesViewValue.NOT_DOWNLOADED;
	}

	return StatusesViewValue.ALL;
}

function toSearchItemRequest(v: SearchItemRequestViewModel): SearchItemPageRequest {
	return {
		...v,
		status: toStatus(v.status),
		tags: v.tags.split(',').map(t => ({ id: t, name: t })),
		sort: [v.sort]
	};
}
