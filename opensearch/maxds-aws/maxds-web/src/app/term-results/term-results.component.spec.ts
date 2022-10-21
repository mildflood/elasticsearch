import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TermResultsComponent } from './term-results.component';

describe('TermResultsComponent', () => {
  let component: TermResultsComponent;
  let fixture: ComponentFixture<TermResultsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TermResultsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TermResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
