import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeaturehomeComponent } from './featurehome.component';

describe('FeaturehomeComponent', () => {
  let component: FeaturehomeComponent;
  let fixture: ComponentFixture<FeaturehomeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FeaturehomeComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeaturehomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
