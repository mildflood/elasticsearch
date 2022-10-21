import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SharePreferencesComponent } from './share-preferences.component';

describe('SharePreferencesComponent', () => {
  let component: SharePreferencesComponent;
  let fixture: ComponentFixture<SharePreferencesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SharePreferencesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SharePreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
