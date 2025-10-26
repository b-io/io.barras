from typing import Annotated, get_args, get_origin

from nutil.common import *


def get_type_hints(x: Any) -> Dict[str, Any]:
    """
    Returns the resolved type hints for `x`, handling forward references and Python version
    differences robustly.

    Tries `inspect.get_annotations(obj, eval_str=True)` on Python 3.10+ and falls back to
    `typing.get_type_hints` when unavailable.

    Complexity:
        O(n) in the number of annotations, with constant-time dictionary operations.
    """
    try:
        return inspect.get_annotations(obj, eval_str=True)
    except (AttributeError, TypeError, NameError):
        try:
            from typing import get_type_hints

            glb = getattr(x, "__globals__", None)
            return get_type_hints(x, globalns=glb)
        except Exception:
            return {}


# TODO: use is_dict, is_list, is_set, etc. and make sure they check against collection ABC...
def matches_type_hints(value: Any, annotation: Any, sample_limit: int = 1) -> bool:
    """
    Determines whether the `value` conforms to the `annotation` (PEP 484/585/604), including
    parametrized containers and unions. Validates element types recursively.

    Notes:
        • Accepts `Any`, `Union[...]` (incl. `X | Y`), `Annotated[T, ...]`, `Literal[...]`,
          `Type[T]`, `tuple[int, ...]`, `Sequence[T]`, `Mapping[K, V]`, etc.
        • For iterables, it samples up to `_SAMPLE_LIMIT` elements (may consume from one-shot
          iterators).

    Complexity:
        Linear in container sizes; union checks are O(k) in the number of union branches.
    """
    if annotation is Any:
        return True

    origin = get_origin(annotation)
    args = get_args(annotation)

    if is_null(origin):
        # PEP 604 `X | Y` may surface as `types.UnionType` on some versions
        if (
            getattr(annotation, "__module__", "") == "types"
            and getattr(annotation, "__qualname__", "") == "UnionType"
        ):
            return any(matches_type_hints(value, a) for a in args)
        try:
            return isinstance(value, annotation)
        except TypeError:
            return False
    elif origin is Annotated:
        return matches_type_hints(value, args[0])
    elif origin is Literal:
        return any(value == a for a in args)
    elif origin is Union:
        return any(matches_type_hints(value, a) for a in args)
    elif is_type(origin):
        return isinstance(value, type) and (not args or issubclass(value, args[0]))
    elif is_tuple(origin):
        if not isinstance(value, tuple):
            return False
        if (
            len(args) == 2 and args[1] is Ellipsis
        ):  # variable-length homogeneous tuple: `tuple[T, ...]`
            (elem_type, _) = args
            return all(matches_type_hints(v, elem_type) for v in value)
        if len(args) != len(value):
            return False
        return all(matches_type_hints(v, t) for v, t in zip(value, args))
    elif is_list(origin):
        if not is_list(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        return all(matches_type_hints(v, elem_type) for v in value)
    elif is_sequence(origin):
        # Excludes `tuple` (handled above)
        if not is_sequence(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        return all(matches_type_hints(v, elem_type) for v in value)
    elif is_mapping(origin):
        if not is_mapping(value):
            return False
        if not args:
            return True
        key_type, val_type = args
        return all(
            matches_type_hints(k, key_type) and matches_type_hints(v, val_type)
            for k, v in value.items()
        )
    elif is_set(origin):
        if not is_set(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        return all(matches_type_hints(v, elem_type) for v in value)
    elif is_iterable(origin):
        if not is_iterable(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        # Check up to `sample_limit` items
        has_item, first_item, it = peek(value)
        if not has_item:
            return True
        if not matches_type_hints(first_item, elem_type):
            return False
        checked = 1
        for x in it:
            if not matches_type_hints(x, elem_type):
                return False
            checked += 1
            if checked >= sample_limit:
                break
        return True
    try:
        return isinstance(value, origin)
    except TypeError:
        return False


def flatten_expected_types(annotation: Any) -> Tuple[Any, ...]:
    """
    Normalizes the `annotation` into a `tuple` of acceptable alternatives for display or downstream
    formatting (for example, wrapping into `ExpectedTypeList` elsewhere).

    Dispatch:
        • `Union[int, str]`      → `(int, str)`
        • `Optional[int]`        → `(int, NoneType)`
        • `Annotated[T, ...]`    → same as `flatten_expected_types(T)`
        • `Literal[1, 2, "x"]`   → `(int, int, str)` (the raw literal values are not returned here)
        • `list[int]`            → `(list,)`  (container element typing is not expanded)
        • `int`                  → `(int,)`

    Complexity:
        O(k) for `Union`/`Annotated`/`Literal` unwrapping; otherwise O(1).
    """
    origin = get_origin(annotation)
    args = get_args(annotation)

    if origin is Annotated:
        return flatten_expected_types(args[0])
    elif origin is Literal:
        return tuple(type(v) for v in args) if args else (annotation,)
    elif origin is Union:
        return tuple(args)  # includes `NoneType` when `Optional[T]` is `Union[T, NoneType]`
    return (annotation,)
